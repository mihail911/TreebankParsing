#!/usr/bin/env python

class Result:
  def __init__(self):
    self.gold = ''
    self.guess = ''
    self.scores = ''
    self.f1 = -1

  @classmethod
  def from_str(cls, str):
    r = Result()
    terms = str.split("Gold:")
    r.guess = terms[0].strip()
    terms = terms[1].split("[Current]")
    r.gold = terms[0].strip()
    terms = terms[1].split("[Average]")
    r.scores = terms[0].strip()
    terms = r.scores.split("F1:")
    terms = terms[1].split("EX:")
    r.f1 = float(terms[0].strip())

    return r




import sys

if len(sys.argv) not in [2,3]:
  print "python analyze.py <parser dump> [f1 threshold in percentage]"
  sys.exit(-1)
else:
  fname = sys.argv[1]

threshold = 70.
if len(sys.argv) == 3:
  threshold = int(sys.argv[2])

with open(fname, 'rb') as f:
  raw = f.read()
  raw_results = raw.split('Guess:')[1:]
  results = [Result.from_str(r) for r in raw_results]

bad = [r for r in results if r.f1 < threshold]
print 'found', len(bad), 'bad guesses with f1 < ', threshold
for b in bad:
  print "Score:"
  print b.scores
  print "Gold:"
  print b.gold
  print "Guess:"
  print b.guess
  print


