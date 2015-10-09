nxjc Main.java
#nxjc */*.java
nxjlink -o Main.nxj Main
rm *.class
rm */*.class
sudo env "PATH=$PATH" nxjupload Main.nxj
