nxjc Main.java
nxjc */*.java
nxjlink -o Auflauf15.nxj Main
rm *.class
rm */*.class
sudo env "PATH=$PATH" nxjupload Auflauf15.nxj
rm Auflauf15.nxj
