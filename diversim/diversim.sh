 #!/bin/bash

baseDir=$HOME/Diversify/GIT/diversify-simulation/diversim
binDir=$baseDir/target/classes/ #$HOME/workspace/diversim/bin # where are the binaries
libDir=$HOME/Diversify/mason # where are the libs
libs="$baseDir/lib/*:$libDir/*" #jmf.jar:$libDir/itext-1.2.jar:$libDir/portfolio.jar:$libDir/jcommon-1.0.16.jar:$libDir/jfreechart-1.0.13.jar"

steps=1000000
time=1000

#diversim_cmdLine="java -Xmx256M -cp $libs:$binDir diversim.model.Fate" # test distributions
diversim_cmdLine="sudo java -Xmx256M -cp $libs:$binDir diversim.BipartiteGraphWithUI" # GUI
#diversim_cmdLine="java -Xmx256M -cp $libs:$binDir diversim.model.BipartiteGraph" # NO GUI

diversim_params= #"-for $steps -time $time" #-for 2000 # 2>/dev/null # -seed 1 -time 1000

if [ $# -lt 1 ] ; then
   eval $diversim_cmdLine $diversim_params
else
  eval $diversim_cmdLine $@
fi
