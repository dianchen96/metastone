import lmdb
import struct
from itertools import izip

def grouped(iterable, n):
    "s -> (s0,s1,s2,...sn-1), (sn,sn+1,sn+2,...s2n-1), (s2n,s2n+1,s2n+2,...s3n-1), ..."
    return izip(*[iter(iterable)]*n)

def to_byte_array(string):
	return map(ord, string)

def to_float_array(string):
	return [struct.unpack('>f', ''.join(b))[0] for b in grouped(string, 4)]



class PairLMDB:
	def __init__(self, name):
		self.name = name
		self.DB = lmdb.open(name, 99999999999)
		self.i = self.get_num_elements()

	def get_num_elements(self):
		return int(self.DB.stat()['entries'])

	def pairs(self):
		with self.DB.begin() as txn:
			cursor = txn.cursor()
			for key, datum in cursor:
				datum = to_float_array(datum)
				state = datum[:339]
				action = datum[339:]
				yield state, action

	def readings(self):
		with self.DB.begin() as txn:
			cursor = txn.cursor()
			for key, datum in cursor:
				yield datum


class LMDB:
	def __init__(self, name):
		self.name = name
		self.DB = lmdb.open(name, 99999999999)
		self.i = self.get_num_elements()

	def get_num_elements(self):
		return int(self.DB.stat()['entries'])

	def save(self, key, datum):
		str_id = '{:08}'.format(key)
		with self.DB.begin(write=True) as txn:
			txn.put(str_id.encode('ascii'), datum)

