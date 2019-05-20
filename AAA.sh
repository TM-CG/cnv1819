export LOADBALANCER=18.197.149.63

testeS1() {
  set -o xtrace
  time curl 'http://'$LOADBALANCER':8001/climb?w=512&h=512&x0=0&x1=512&y0=0&y1=512&xS=450&yS=400&s=BFS&i=datasets/RANDOM_HILL_512x512_2019-02-27_09-46-42.dat' > S1.png
  set +o xtrace
}

testeL1() {
  set -o xtrace
  time curl 'http://'$LOADBALANCER':8001/climb?w=512&h=512&x0=0&x1=512&y0=0&y1=512&xS=0&yS=0&s=BFS&i=datasets/RANDOM_HILL_512x512_2019-02-27_09-46-42.dat' > L1.png
  set +o xtrace
}

testeL2(){
  set -o xtrace
  time curl 'http://'$LOADBALANCER':8001/climb?w=512&h=512&x0=0&x1=512&y0=0&y1=512&xS=0&yS=0&s=BFS&i=datasets/RANDOM_HILL_512x512_2019-02-27_09-46-42.dat' > L2.png
  set +o xtrace
}

testeL3(){
  set -o xtrace
  time curl 'http://'$LOADBALANCER':8001/climb?w=512&h=512&x0=0&x1=512&y0=0&y1=512&xS=0&yS=0&s=BFS&i=datasets/RANDOM_HILL_512x512_2019-02-27_09-46-42.dat' > L3.png
  set +o xtrace
}

testeXL1(){
  set -o xtrace
  time curl 'http://'$LOADBALANCER':8001/climb?w=1024&h=1024&x0=0&x1=1024&y0=0&y1=1024&xS=1000&yS=1000&s=BFS&i=datasets/RANDOM_HILL_1024x1024_2019-03-08_16-57-28.dat' > XL1.png
  set +o xtrace
}