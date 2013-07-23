#!/usr/bin/python

import argparse
import base64
import getpass
import json
import shlex
from subprocess import Popen, PIPE
import sys

# This script calls the github api to generate an auth token for use with the
# Issue plugin

def gen_auth(args):
	parser = argparse.ArgumentParser()
	parser.add_argument("--username", help="Your github username")
	parser.add_argument("--password", help="Your github password")
	args = parser.parse_args(args)

	username = args.username
	password = args.password

	if username is None:
		username = raw_input("GitHub username: ")
	if password is None:
		password = getpass.getpass("GitHub password: ")

	basic_auth = base64.b64encode(username + ":" + password)
	cmd = "curl -H 'Content-Type: application/x-www-form-urlencoded' -H 'Authorization: Basic " + basic_auth + "' -d '{\"client_id\": \"c93be78ae017864c5821\", \"client_secret\": \"b3fac62129c509911ebfc02b44d4669ebfcb8804\", \"scopes\": [\"public_repo\"]}' -X POST https://api.github.com/authorizations"
	args = shlex.split(cmd)
	proc = Popen(args, stdout=PIPE, stdin=PIPE, stderr=PIPE)
	(out, err) = proc.communicate()

	data = json.loads(out)
	auth_token = data['token']

	f = open('auth_token', 'w')
	f.write(auth_token + '\n')
	f.close()

if __name__ == "__main__":
	gen_auth(sys.argv[1:])
