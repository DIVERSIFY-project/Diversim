#!/bin/bash

binDir=$HOME/ownCloud/neutral_model/diversify/diversim/src/main/java # where 
are the binaries
libDir=$HOME/ownCloud/tcd/mason # where are the libs

libs=$libDir/jmf.jar:$libDir/itext-1.2.jar:$libDir/portfolio.jar:$libDir/jcommon-1.0.16.jar:$libDir/jfreechart-1.0.13.jar:$libDir/jar/mason.17.jar

diversim_cmdLine="java -Xmx256M -cp $binDir:$libs diversim.BipartiteGraph" # WithUI
diversim_params="-for 2000" # 2>/dev/null -seed 1 -time 1000 -for 200000

if [ $# -lt 1 ] ; then
  eval $diversim_cmdLine $diversim_params
else
  eval $diversim_cmdLine $@
fi
