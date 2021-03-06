#!/bin/sh
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`
if [ "$1" = "" ]
  then if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -DSWT_GTK3=0 -Xms@vm.initialHeapSize@m -Xmx@vm.maxHeapSize@m -D@mainClass@.main.mode=gui -classpath "$PRGDIR/@linux.jarFileName@:$PRGDIR/lib/*" @mainClass@
  else java -DSWT_GTK3=0 -Xms@vm.initialHeapSize@m -Xmx@vm.maxHeapSize@m -D@mainClass@.main.mode=gui -classpath "$PRGDIR/@linux.jarFileName@:$PRGDIR/lib/*" @mainClass@
  fi
else
  if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -Xms@console.vm.initialHeapSize@m -Xmx@console.vm.maxHeapSize@m -D@mainClass@.main.mode=console -classpath "$PRGDIR/@linux.jarFileName@:$PRGDIR/lib/*" @mainClass@ "$@"
  else java -Xms@console.vm.initialHeapSize@m -Xmx@console.vm.maxHeapSize@m -D@mainClass@.main.mode=console -classpath "$PRGDIR/@linux.jarFileName@:$PRGDIR/lib/*" @mainClass@ "$@"
  fi
fi
