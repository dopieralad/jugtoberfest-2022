import apache_beam
import hashlib


class HashStrings(apache_beam.PTransform):
    def expand(self, strings):
        return strings | "Hash strings" >> apache_beam.Map(lambda string: self.hash(string))

    def hash(self, string):
        hash = hashlib.md5(string.encode())
        return hash.hexdigest()
