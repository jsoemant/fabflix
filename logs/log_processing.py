print("hello")
paths = ['1.txt', '2.txt', '3.txt', '4.txt']
for path in paths:
    lines = [line.strip() for line in open('logs/' + path)]



    time1 = []
    time2 = []
    for i in lines:
        line = i.split("|")
        time1.append(int(line[0].strip()))
        time2.append(int(line[1].strip()))

    print(path)
    print((sum(time1)/len(time1))/1000000)
    print((sum(time2)/len(time2))/1000000)