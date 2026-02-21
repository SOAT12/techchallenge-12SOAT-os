import os

def fix_it(p):
    try:
        if not os.path.isfile(p):
            return
        with open(p, 'rb') as f:
            data = f.read()
        new_data = data.replace(b'tc_group_7', b'os')
        if new_data != data:
            with open(p, 'wb') as f:
                f.write(new_data)
            print(f"FIXED: {p}")
    except Exception as e:
        print(f"ERROR: {p} - {e}")

if __name__ == "__main__":
    root = r"c:\Users\alexp\pos_graduacao\techchallenge-12SOAT-os"
    for r, d, files in os.walk(root):
        for f in files:
            if f.endswith(('.java', '.xml', '.yaml', '.yml', '.properties', '.md')):
                fix_it(os.path.join(r, f))
