import sys
import random
from database import PairLMDB, LMDB


DATA_LOCATION = "/Users/dianchen/metastone/training/data/"
DATASET_LOCATION = "/Users/dianchen/metastone/training/datasets/"
TRAIN_DATA = ["run_" + str(i) for i in range(0, 5)]
TEST_DATA = ["run_" + str(i) for i in range(5, 10)]

def get_runs(runs, stop=True):
    while True:
        random.shuffle(runs)
        for r in runs:
            db = PairLMDB(DATA_LOCATION + r)
            print "Loading from %s, total size: %d" % (r, db.i)
            yield db.readings()
        if stop:
            break

def build_data(dataset, dataset_name):
	location = DATASET_LOCATION + dataset_name
	used = set([])

	db_set = LMDB(location)
	N = 0

	for iterators in get_runs(dataset, True):
		for datum in iterators:
			key = random.getrandbits(30)
			while key in used:
				key = random.getrandbits(30)

			N += 1
			if N % 1000 == 0:
				print "Wrote ", N

			used.add(key)
			db_set.save(key, datum)


	print "Wrote", dataset_name, "size", N
	print "total DB size", db_set.get_num_elements()




if __name__ == "__main__":
	build_data(TRAIN_DATA, sys.argv[1]+ "/train")
	build_data(TEST_DATA, sys.argv[1]+ "/test")