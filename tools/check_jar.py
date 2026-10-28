"""Static release checks for a NeoForge mod jar.

Usage: python check_jar.py <mod.jar> <expected_mod_id> <expected_version>
Exit code 0 = all checks passed, 1 = at least one failure.
"""
import io
import json
import re
import sys
import zipfile

jar_path, mod_id, version = sys.argv[1], sys.argv[2], sys.argv[3]
fails, notes = [], []


def fail(msg):
    fails.append(msg)


def parse_version(v):
    # Like Maven's ComparableVersion: tokens with their separator.
    # A letter/digit switch without separator counts as "-".
    out = []
    prev = None
    for sep, item in re.findall(r"([.\-+_]?)([0-9]+|[A-Za-z]+)", v):
        if not sep:
            sep = "-" if prev is not None else ""
        if sep in "+_":
            sep = "-"
        out.append((sep, int(item) if item.isdigit() else item.lower()))
        prev = item
    return out


def _item_cmp(a, b):
    if isinstance(a, int) and isinstance(b, int):
        return (a > b) - (a < b)
    if isinstance(a, int):
        return 1  # number beats qualifier
    if isinstance(b, int):
        return -1
    return (a > b) - (a < b)


def cmp(a, b):
    pa, pb = parse_version(a), parse_version(b)
    for i in range(max(len(pa), len(pb))):
        if i >= len(pa) or i >= len(pb):
            longer, sign = (pb, -1) if i >= len(pa) else (pa, 1)
            sep, item = longer[i]
            if isinstance(item, int):
                return sign if (sep == "." and item > 0) or sep == "-" else 0
            return -sign  # trailing qualifier (alpha, beta) is older
        (sa, ia), (sb, ib) = pa[i], pb[i]
        if sa != sb:
            # "." (next number) ranks above "-" (sub list / qualifier)
            return 1 if sa == "." else -1
        c = _item_cmp(ia, ib)
        if c:
            return c
    return 0


def in_range(v, rng):
    rng = rng.strip()
    if not rng or rng[0] not in "[(":
        return cmp(v, rng) >= 0  # soft requirement = minimum
    lo_inc, hi_inc = rng[0] == "[", rng[-1] == "]"
    lo, _, hi = rng[1:-1].partition(",")
    lo, hi = lo.strip(), hi.strip()
    if not _ and lo:  # [1.0] exact
        return cmp(v, lo) == 0
    if lo and (cmp(v, lo) < 0 or (cmp(v, lo) == 0 and not lo_inc)):
        return False
    if hi and (cmp(v, hi) > 0 or (cmp(v, hi) == 0 and not hi_inc)):
        return False
    return True


z = zipfile.ZipFile(jar_path)
names = z.namelist()

# 1. mods.toml
toml_name = "META-INF/neoforge.mods.toml"
if toml_name not in names:
    fail("neoforge.mods.toml fehlt")
    toml = ""
else:
    toml = z.read(toml_name).decode("utf-8")
    if not re.search(r'modId\s*=\s*"%s"' % re.escape(mod_id), toml):
        fail(f"mods.toml: modId {mod_id} nicht gefunden")
    if not re.search(r'version\s*=\s*"%s"' % re.escape(version), toml):
        fail(f"mods.toml: version {version} nicht gefunden")
    if "${" in toml:
        fail("mods.toml enthält nicht ersetzte Platzhalter ${...}")
    deps = re.findall(r'modId\s*=\s*"([^"]+)"\s*\n\s*type\s*=\s*"([^"]+)"\s*\n\s*versionRange\s*=\s*"([^"]+)"', toml)
    notes.append("Abhängigkeiten: " + ", ".join(f"{m} {t} {r}" for m, t, r in deps))
    logo = re.search(r'logoFile\s*=\s*"([^"]+)"', toml)
    if logo and logo.group(1) not in names:
        fail(f"Logo {logo.group(1)} fehlt im Jar")

# 2. Embedded jars (jarJar): every embedded version must sit inside its declared range
meta_name = "META-INF/jarjar/metadata.json"
embedded = [n for n in names if n.startswith("META-INF/jarjar/") and n.endswith(".jar")]
if meta_name in names:
    meta = json.loads(z.read(meta_name))
    for j in meta.get("jars", []):
        ident = j["identifier"]
        art = f'{ident["group"]}:{ident["artifact"]}'
        ver = j["version"]["artifactVersion"]
        rng = j["version"]["range"]
        path = j["path"]
        if path not in names:
            fail(f"jarJar: {art} verweist auf fehlende Datei {path}")
        if not in_range(ver, rng):
            fail(f"jarJar: {art} Version {ver} liegt NICHT im Bereich {rng} (dieser Fehler hat 1.0.0 kaputt gemacht)")
        notes.append(f"eingebettet: {art} {ver} in {rng}")
elif embedded:
    fail("Eingebettete Jars ohne metadata.json")
if not embedded:
    notes.append("keine eingebetteten Jars")
for n in embedded:
    lower = n.lower()
    for lib in ("registrate", "create-", "flywheel", "ponder", "catnip"):
        if lib in lower:
            fail(f"{n}: eingebettet, obwohl Create das selbst mitbringt")

# 3. Namespaces and leftovers
spaces = sorted({n.split("/")[1] for n in names if n.startswith(("assets/", "data/")) and n.count("/") >= 2})
notes.append("Namensräume: " + ", ".join(spaces))
for bad in ("createaviation",):
    hits = [n for n in names if bad in n]
    if hits:
        fail(f"Alter Name '{bad}' in Pfaden: {hits[:3]}")

# 4. Class files target Java 21 (major 65) or lower
majors = set()
for n in names:
    if n.endswith(".class"):
        data = z.read(n)[:8]
        majors.add(int.from_bytes(data[6:8], "big"))
if not majors:
    fail("Keine Klassen im Jar")
elif max(majors) > 65:
    fail(f"Klassen für Java > 21 kompiliert (major {max(majors)})")
notes.append(f"Klassen: {sum(n.endswith('.class') for n in names)}, Java-Major {sorted(majors)}")

# 5. Lang: every block/item key referenced by models has a translation
lang_name = f"assets/{mod_id}/lang/en_us.json"
if lang_name not in names:
    fail("en_us.json fehlt")
else:
    lang = json.loads(z.read(lang_name))
    def direct(prefix):
        # Only files directly in the folder; sub-folders hold partial models
        return {n[len(prefix):-5] for n in names
                if n.startswith(prefix) and n.endswith(".json") and "/" not in n[len(prefix):]}
    blocks = direct(f"assets/{mod_id}/blockstates/")
    items = direct(f"assets/{mod_id}/models/item/")
    missing = [b for b in sorted(blocks) if f"block.{mod_id}.{b}" not in lang]
    missing += [i for i in sorted(items - blocks) if f"item.{mod_id}.{i}" not in lang and f"block.{mod_id}.{i}" not in lang]
    if missing:
        fail(f"Übersetzung fehlt für: {missing[:10]}")
    notes.append(f"Blockstates: {len(blocks)}, Item-Modelle: {len(items)}, Lang-Einträge: {len(lang)}")

# 6. JSON files parse
bad_json = []
for n in names:
    if n.endswith(".json") or n.endswith(".mcmeta"):
        try:
            json.loads(z.read(n))
        except Exception:
            bad_json.append(n)
if bad_json:
    fail(f"Ungültiges JSON: {bad_json[:5]}")

# 7. License
if not any(n.upper().startswith("LICENSE") for n in names):
    fail("Keine Lizenzdatei im Jar")

for n in notes:
    print("  ·", n)
if fails:
    print("FEHLGESCHLAGEN:")
    for f in fails:
        print("  ✗", f)
    sys.exit(1)
print("ALLE PRÜFUNGEN BESTANDEN")
