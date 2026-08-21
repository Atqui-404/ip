# UI Test Plan

This file records the console (UI) test cases for the app, run by the
`test-ui` skill (`.claude/skills/test-ui/SKILL.md`).

## Format

Each test case is a heading followed by an aim and a table of
input/expected-output pairs:

```
## TC<id>: <short title>
**Aim:** <what this test case checks, and why>

| Input | Expected Output |
|---|---|
| `command` | `text that must appear in the console output after this command` |
```

Notes:

* All rows in one test case are sent, in order, to a single fresh run of the
  program, so a test case can be a whole scenario (e.g. add a task, then
  list, then mark it done, then list again).
* The "Expected Output" check is a **substring search performed in order**:
  it must appear in the console output at or after the previous match. It is
  not required to be the *entire* output produced by that command, just
  distinguishing text that proves the command worked.
* Use the literal characters `\n` inside a cell to require a newline inside
  the expected text (e.g. `1.[ ] read book\n2.[ ] write code`).
* You do not need to add a final `bye` row - the test runner appends one
  automatically to end the session cleanly if the last row isn't already
  `bye`.
* TC ids don't need to be numeric or sequential; keep them short and unique.
* Error cases are interleaved next to the valid-input case for the same
  command (rather than grouped in one section), so a change that breaks a
  valid case while "fixing" an error case (or vice versa) gets caught.

## Test Cases

## TC1: Listing tasks when none have been added
**Aim:** `list` should tell the user they have no tasks, instead of printing an empty list.

| Input | Expected Output |
|---|---|
| `list` | `You have no tasks!` |

## TC2: Unrecognized commands are rejected, not silently stored
**Aim:** Level-5: as of this level, input that isn't a recognised command (`list`, `todo`, `deadline`, `event`, `mark`, `unmark`, `bye`) is an error, not a plain task - this replaces the old Level-2/3 "any text becomes a task" fallback.

| Input | Expected Output |
|---|---|
| `read book` | `OOPS!!! I don't understand that` |
| `list` | `You have no tasks!` |

## TC2b: A valid todo still works right after a rejected command
**Aim:** The error path for TC2 shouldn't corrupt state - a valid `todo` sent immediately afterwards must still succeed and be the only task stored.

| Input | Expected Output |
|---|---|
| `read book` | `OOPS!!! I don't understand that` |
| `todo read book` | `Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.` |
| `list` | `You have 1 tasks!\n1.[T][ ] read book` |

## TC3: Marking and unmarking a task
**Aim:** `mark <n>` flips a task to done (`[X]`), and `unmark <n>` flips it back to not done (`[ ]`).

| Input | Expected Output |
|---|---|
| `todo read book` | `Got it. I've added this task:` |
| `mark 1` | `Nice! I've marked this task as done:\n  [T][X] read book` |
| `unmark 1` | `OK, I've marked this task as not done yet:\n  [T][ ] read book` |

## TC4: mark/unmark error handling
**Aim:** Level-5/A-Exceptions: `mark`/`unmark` must reject a missing index, a non-numeric index, and an out-of-range index, each with its own `OOPS!!!` message, instead of the old `ERROR:` wording.

| Input | Expected Output |
|---|---|
| `todo read book` | `Got it. I've added this task:` |
| `mark` | `OOPS!!! Tell me which task number to mark` |
| `mark abc` | `OOPS!!! 'abc' isn't a task number I understand` |
| `mark 5` | `OOPS!!! There is no task 5 - you currently have 1 task.` |
| `unmark` | `OOPS!!! Tell me which task number to unmark` |
| `unmark abc` | `OOPS!!! 'abc' isn't a task number I understand` |
| `unmark 5` | `OOPS!!! There is no task 5 - you currently have 1 task.` |
| `list` | `1.[T][ ] read book` |

## TC5: Saying goodbye
**Aim:** `bye` ends the session with a farewell message.

| Input | Expected Output |
|---|---|
| `bye` | `Bye! See you soon!` |

## TC6: Adding a todo
**Aim:** `todo <desc>` adds a task tagged `[T]` with no date/time, and reports the running total.

| Input | Expected Output |
|---|---|
| `todo borrow book` | `Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 1 tasks in the list.` |
| `list` | `1.[T][ ] borrow book` |

## TC6b: todo with an empty description is an error
**Aim:** Level-5: `todo` with no description must be rejected instead of creating a blank task - the spec's own minimal example.

| Input | Expected Output |
|---|---|
| `todo` | `OOPS!!! The description of a todo can't be empty` |
| `list` | `You have no tasks!` |

## TC7: Adding a deadline
**Aim:** `deadline <desc> /by <when>` adds a task tagged `[D]` with the `by` text shown verbatim (no date parsing at this stage).

| Input | Expected Output |
|---|---|
| `deadline return book /by Sunday` | `Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 1 tasks in the list.` |
| `deadline do homework /by no idea :-p` | `[D][ ] do homework (by: no idea :-p)` |

## TC7b: deadline error handling
**Aim:** Level-5: a `deadline` missing `/by` entirely, missing its description, or missing the due time after `/by` must each be rejected with a distinct message.

| Input | Expected Output |
|---|---|
| `deadline return book` | `OOPS!!! A deadline needs a due time` |
| `deadline /by Sunday` | `OOPS!!! The description of a deadline can't be empty` |
| `deadline return book /by` | `OOPS!!! The due time after` |
| `list` | `You have no tasks!` |

## TC8: Adding an event
**Aim:** `event <desc> /from <start> /to <end>` adds a task tagged `[E]` with both the start and end text shown verbatim.

| Input | Expected Output |
|---|---|
| `event project meeting /from Mon 2pm /to 4pm` | `Got it. I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 1 tasks in the list.` |

## TC8b: event error handling
**Aim:** Level-5: an `event` missing `/from`, missing `/to`, missing its description, or missing either time must each be rejected with a distinct message.

| Input | Expected Output |
|---|---|
| `event project meeting` | `OOPS!!! An event needs a start time` |
| `event /from Mon /to Tue` | `OOPS!!! The description of an event can't be empty` |
| `event meeting /from Mon` | `OOPS!!! An event needs an end time` |
| `event meeting /from /to 4pm` | `OOPS!!! The start time after` |
| `event meeting /from Mon /to` | `OOPS!!! The end time after` |
| `list` | `You have no tasks!` |

## TC9b: Sub-command markers are case-insensitive
**Aim:** `/by`, `/from`, and `/to` must be recognised regardless of casing (e.g. `/BY`, `/From`, `/TO`).

| Input | Expected Output |
|---|---|
| `deadline return book /BY Sunday` | `[D][ ] return book (by: Sunday)` |
| `event project meeting /From Mon 2pm /TO 4pm` | `[E][ ] project meeting (from: Mon 2pm to: 4pm)` |

## TC9: Marking a typed task shows its type tag
**Aim:** Marking/unmarking a `Deadline`/`Event`/`Todo` must still show its `[D]`/`[E]`/`[T]` tag and date info, not just the bare description, since the mark/unmark output is now built polymorphically from the task itself.

| Input | Expected Output |
|---|---|
| `event project meeting /from Mon 2pm /to 4pm` | `added this task` |
| `mark 1` | `Nice! I've marked this task as done:\n  [E][X] project meeting (from: Mon 2pm to: 4pm)` |
| `unmark 1` | `OK, I've marked this task as not done yet:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)` |
