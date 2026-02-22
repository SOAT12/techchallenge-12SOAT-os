import os

root_dir = r"c:\Users\alexp\pos_graduacao\techchallenge-12SOAT-os\src\test\java"
old_name = "tc_group_7"
new_name = "os"

print(f"Searching in: {root_dir}")

for root, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                if old_name in content:
                    print(f"Updating {file_path}")
                    new_content = content.replace(old_name, new_name)
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    print(f"Successfully updated {file}")
            except Exception as e:
                print(f"Error processing {file_path}: {e}")
