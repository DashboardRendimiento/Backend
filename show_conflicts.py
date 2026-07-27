import os
import glob

def find_conflicts():
    for f in glob.glob('src/main/java/**/*.java', recursive=True):
        if not os.path.isfile(f): continue
        with open(f, 'r', encoding='utf-8', errors='ignore') as file:
            content = file.read()
        if '<<<<<<<' in content and '=======' in content and '>>>>>>>' in content:
            print(f"--- CONFLICT IN {f} ---")
            lines = content.split('\n')
            in_conflict = False
            for line in lines:
                if line.startswith('<<<<<<<'):
                    in_conflict = True
                if in_conflict:
                    print(line)
                if line.startswith('>>>>>>>'):
                    in_conflict = False
                    print()
                    
find_conflicts()
