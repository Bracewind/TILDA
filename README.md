# TILDA

The folder dataset contains all the image used for testing the accuracy of the network. 
Please respect the architecture of this folder :
Train (respectively Test) contains only folders that bear the name of the class they represent.
In each class folder, there is pictures representing the object.
If you modify this folder, you have to replace the folder Train and Test inside the assets of the TILDA application.

The folder TILDA contains the application mobile and is ready to use

For a direct use, consider using the apk provided in the APK folder


Warning !!! This repository uses lfs to store the inception network. 

To retrieve the network :
first install git-lfs on ubuntu
sudo apt install git-lfs

then use 
git lfs pull
to retrieve the network.

If it does not work first install lfs on your local git folder using
git lfs install
