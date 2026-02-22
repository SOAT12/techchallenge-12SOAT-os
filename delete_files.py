import os
import glob

def delete_matching_files(directory, pattern):
    for f in glob.glob(os.path.join(directory, '**', pattern), recursive=True):
        if os.path.isfile(f):
            print(f"Deleting: {f}")
            os.remove(f)

if __name__ == '__main__':
    src_dir = r"c:\Users\alexp\pos_graduacao\techchallenge-12SOAT-os\src"
    delete_matching_files(src_dir, '*Stock*')
    delete_matching_files(src_dir, '*ToolCategory*')
    print("Done")
