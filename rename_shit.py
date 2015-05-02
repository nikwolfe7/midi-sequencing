import os
for root, dirs, files in os.walk(".", topdown=False):
	for f in files:
		if f.endswith(".mid"):
			path = os.path.abspath(os.path.join(root,f))
			newName = f.replace(" ","-").replace("_","-").replace(",","")
			new_path = os.path.abspath(os.path.join(root,newName))
			print(new_path)
			os.rename(path,new_path)
