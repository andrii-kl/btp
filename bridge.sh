if [ $# -eq 2 ]
then
    SOURCE=$1
    DESTINATION=$2
else
    echo "source and destination chain is mandatory"
    exit
fi

if !(command -v bazel &> /dev/null)
then
    echo "bazel is not found in path. Please follow https://bazel.build"
    exit
fi

bazel run "@${SOURCE}//:${SOURCE}_node_image"
bazel run "@${DESTINATION}//:${DESTINATION}_node_image"
bazel run @btp//cmd/btpsimple:btpsimple_image

bazel run "@btp//bridge:${SOURCE}_and_${DESTINATION}" --verbose_failures --define network=testnet
