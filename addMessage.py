#!/usr/bin/env python3

# Creates Java files for request, response messages and unit tests based on templates

import jinja2
from pprint import pprint
import json
import sys
import re
import os

CATEGORIES = ["authentication", "currentStatus", "historyLog", "control", "controlStream"]
CATEGORY_UUID = {
    "authentication": "AUTHENTICATION",
    "currentStatus": "CURRENT_STATUS",
    "historyLog": "HISTORY_LOG",
    "control": "CONTROL",
    "controlStream": "CONTROL_STREAM"
}

RESPONSE_ONLY_CATEGORIES = ["historyLog", "controlStream"]

MAIN_TEMPLATES = {
  "messages/src/main/java/com/jwoglom/pumpx2/pump/messages/request/{prefix}template.j2": \
    "messages/src/main/java/com/jwoglom/pumpx2/pump/messages/request/{cat}/{requestName}.java",

  "messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/{prefix}template.j2": \
    "messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/{cat}/{responseName}.java",
}

TEST_TEMPLATES = {
  "messages/src/test/java/com/jwoglom/pumpx2/pump/messages/request/{prefix}template.j2": \
    "messages/src/test/java/com/jwoglom/pumpx2/pump/messages/request/{cat}/{requestName}Test.java",

  "messages/src/test/java/com/jwoglom/pumpx2/pump/messages/response/{prefix}template.j2": \
    "messages/src/test/java/com/jwoglom/pumpx2/pump/messages/response/{cat}/{responseName}Test.java"
}

TEMPLATES = {
  **MAIN_TEMPLATES,
  **TEST_TEMPLATES
}

MESSAGES_ENUM = "messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java"
HISTORY_LOG_MESSAGES_ENUM = "messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/historyLog/HistoryLogParser.java"


def render(file, ctx):
  return jinja2.Environment(loader=jinja2.BaseLoader()).from_string(open(file, 'r').read()).render(**ctx)


def add_args(ctx, opt):
  ctx[opt + "Args"] = []
  ctx[opt + "Size"] = 0

  while True:
    arg = {}
    name = input('Arg name: ')
    if not name or len(name) < 2:
      print('No more arguments')
      break
    arg["name"] = name
    typ = input('Type or size? ').lower()
    if typ == 'bool' or typ == 'boolean':
      arg["size"] = 1
      arg["type"] = 'boolean'
    elif typ in ('b'):
      arg["size"] = 1
      arg["type"] = 'int'
    elif typ in ('short', 'int', 'h'):
      arg["size"] = 2
      arg["type"] = 'int'
    elif typ in ('uint32', 'long', 'i'):
      arg["type"] = 'long'
      arg["size"] = 4
    elif typ in ('float', 'f'):
      arg["type"] = 'float'
      arg["size"] = 4
    elif typ == 'uint64' or typ == 'biginteger' or typ == '8':
      arg["size"] = 8
      arg["type"] = 'BigInteger'
    elif typ == 'string' or typ == 'str':
      arg["size"] = int(input("String length: "))
      arg["type"] = 'String'
    else:
      arg["size"] = int(typ)
      arg["type"] = 'int'
    arg["index"] = int(input('Index into sequence: '))
    ctx[opt + "Args"].append(arg)
    ctx[opt + "Size"] += arg["size"]
  
  return ctx

def build_ctx():
  if "--json" in sys.argv:
    j = input('Enter JSON? ')
    if j and len(j) > 4:
      return json.loads(j)

  ctx = {}
  mname = None
  while not mname or mname.lower().endswith("request") or mname.lower().endswith("response"):
    mname = input('Message name: ')
  ctx["name"] = mname

  cat = None
  while cat not in CATEGORIES:
    cat = input('Category: ')
  ctx["cat"] = cat
  ctx["catUuid"] = CATEGORY_UUID[cat]

  ctx["requestName"] = ctx["name"] + 'Request'
  if not cat in RESPONSE_ONLY_CATEGORIES:
    ctx["requestOpcode"] = int(input(ctx["requestName"] + ' opcode: '))
    ctx = add_args(ctx, "request")
  else:
    ctx["requestOpcode"] = 0

  if cat != "historyLog":
    ctx["responseName"] = ctx["name"] + 'Response'
  else:
    ctx["responseName"] = ctx["name"] + 'HistoryLog'
  ctx["responseOpcode"] = int(input(ctx["responseName"] + ' opcode: '))
  ctx = add_args(ctx, "response")  

  print(json.dumps(ctx))
  
  return ctx

def addToMessagesEnum(ctx):
  text = open(MESSAGES_ENUM, "r").read()
  before, after = text.split("// MESSAGES_END", 1)

  def camel_to_snake(name):
    name = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', name).upper()


  base_name = ctx["requestName"].split("Request")[0]
  snake_name = camel_to_snake(base_name)
  requestName = ctx["requestName"]
  responseName = ctx["responseName"]
  cat = ctx["cat"]

  importBefore, importAfter = before.split("// IMPORT_END")

  before = importBefore + f"import com.jwoglom.pumpx2.pump.messages.request.{cat}.{requestName};\nimport com.jwoglom.pumpx2.pump.messages.response.{cat}.{responseName};\n// IMPORT_END" + importAfter

  if f"{snake_name}({requestName}.class, {responseName}.class)" in text:
    print("Message enum already exists, skipping")
    return

  full = before + f"{snake_name}({requestName}.class, {responseName}.class),\n    // MESSAGES_END" + after

  with open(MESSAGES_ENUM, "w") as f:
    f.write(full)
  

def addToHistoryLogMessagesEnum(ctx):
  text = open(HISTORY_LOG_MESSAGES_ENUM, "r").read()
  sentinel = "\n        // MESSAGES_END"
  before, after = text.split(sentinel, 1)

  className = ctx["responseName"]

  full = before + f",\n        {className}.class" + sentinel + after

  with open(HISTORY_LOG_MESSAGES_ENUM, "w") as f:
    f.write(full)

def main():
  templates = TEMPLATES
  if "--only-tests" in sys.argv:
    print("Only rendering tests")
    templates = TEST_TEMPLATES

  ctx = build_ctx()

  for tpl, out in templates.items():
    if ctx["cat"] in RESPONSE_ONLY_CATEGORIES and "request" in tpl:
      continue
    f = out.format(**ctx)
    tplName = tpl.format(prefix=f'{ctx["cat"]}/')
    if not os.path.exists(tplName):
      tplName = tpl.format(prefix='')
    print(f'{tplName=}')
    open(f, "w").write(render(tplName, ctx))
    print(f"Wrote {f}")
  
  if ctx["cat"] == "historyLog":
    addToHistoryLogMessagesEnum(ctx)
  else:
    addToMessagesEnum(ctx)

if __name__ == '__main__':
  main()