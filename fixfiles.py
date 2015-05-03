import os

def fix(directory):
	for root, dirs, files in os.walk(directory, topdown=True):
		for dir in dirs:
			if len((root + os.sep + dir).split(os.sep)) <= 2:
				for rewt, dirz, filez in os.walk(root + os.sep + dir):
					for fyle in filez:
						oldpath = os.path.abspath(os.path.join(rewt,fyle))
						newpath = os.path.abspath(os.path.join(root,dir,fyle)) 
						os.rename(oldpath,newpath)
			else:
				os.system("rmdir " + (root + os.sep + dir).replace(" ","\\"))
	lowercase_all(directory)

def lowercase_all(directory):
	for root, dirs, files in os.walk(directory, topdown=True):
		for file in files:
			oldpath = os.path.abspath(os.path.join(root,file))
			if(oldpath.endswith(".zip")):
				os.system("unzip " + oldpath.replace(" ","\\") + " | echo A")
				os.remove(oldpath)
			else:
				newpath = os.path.abspath(os.path.join(root,file.lower().replace("_-_","-").replace("_","-")))
				os.rename(oldpath,newpath)
			

if __name__ == '__main__':
	fix("MIDI_classical")
	fix("MIDI_hip_hop_rap")
	fix("MIDI_rock_metal_country")
	fix("MIDICheck")