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
| `read book` | `ERROR!!! >.<\nThat's an invalid command!` |
| `list` | `You have no tasks!` |

## TC2b: A valid todo still works right after a rejected command
**Aim:** The error path for TC2 shouldn't corrupt state - a valid `todo` sent immediately afterwards must still succeed and be the only task stored.

| Input | Expected Output |
|---|---|
| `read book` | `ERROR!!! >.<\nThat's an invalid command!` |
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
| `mark` | `ERROR!!! >.<\nTell me which task number you want to mark!` |
| `mark abc` | `ERROR!!! >.<\n'abc' isn't a valid task number.` |
| `mark 5` | `ERROR!!! >.<\nThere is no task 5, you currently only have 1 task.` |
| `unmark` | `ERROR!!! >.<\nTell me which task number you want to unmark!` |
| `unmark abc` | `ERROR!!! >.<\n'abc' isn't a valid task number.` |
| `unmark 5` | `ERROR!!! >.<\nThere is no task 5, you currently only have 1 task.` |
| `list` | `1.[T][ ] read book` |

## TC4c: Deleting a task removes it and renumbers the rest
**Aim:** Level-6: `delete <n>` removes that task, confirms with the task's own text, reports the new total, and the remaining tasks shift down to fill the gap.

| Input | Expected Output |
|---|---|
| `todo read book` | `Got it. I've added this task:` |
| `todo write essay` | `Got it. I've added this task:` |
| `todo walk dog` | `Got it. I've added this task:` |
| `delete 2` | `Noted. I've removed this task:\n  [T][ ] write essay\nNow you have 2 tasks in the list.` |
| `list` | `1.[T][ ] read book\n2.[T][ ] walk dog` |

## TC4d: Deleting the first item, then the (now only) last item
**Aim:** Level-6: deleting task 1 from a 2-item list removes the first item and leaves the second as the new task 1; deleting task 1 again (now the original last item) empties the list.

| Input | Expected Output |
|---|---|
| `todo task a` | `Got it. I've added this task:` |
| `todo task b` | `Got it. I've added this task:` |
| `delete 1` | `Noted. I've removed this task:\n  [T][ ] task a\nNow you have 1 tasks in the list.` |
| `list` | `1.[T][ ] task b` |
| `delete 1` | `Noted. I've removed this task:\n  [T][ ] task b\nNow you have 0 tasks in the list.` |
| `list` | `You have no tasks!` |

## TC4e: delete error handling
**Aim:** Level-6/A-Collections: `delete` must reject a missing index, a non-numeric index, and an out-of-range index (reusing the same `parseTaskIndex` validation as mark/unmark), and none of these should remove anything.

| Input | Expected Output |
|---|---|
| `todo read book` | `Got it. I've added this task:` |
| `delete` | `ERROR!!! >.<\nTell me which task number you want to delete!` |
| `delete abc` | `ERROR!!! >.<\n'abc' isn't a valid task number.` |
| `delete 5` | `ERROR!!! >.<\nThere is no task 5, you currently only have 1 task.` |
| `list` | `1.[T][ ] read book` |

## TC4f: mark/unmark on task numbers after a delete has shifted the list
**Aim:** Level-6/A-Collections regression check: after `delete` removes an item and later tasks shift down, `mark`/`list` must operate on the *new* numbering, not stale indices - this specifically catches off-by-one bugs from the array-to-ArrayList switch.

| Input | Expected Output |
|---|---|
| `todo task one` | `Got it. I've added this task:` |
| `todo task two` | `Got it. I've added this task:` |
| `todo task three` | `Got it. I've added this task:` |
| `delete 2` | `Noted. I've removed this task:\n  [T][ ] task two\nNow you have 2 tasks in the list.` |
| `mark 2` | `Nice! I've marked this task as done:\n  [T][X] task three` |
| `list` | `1.[T][ ] task one\n2.[T][X] task three` |

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
| `todo` | `ERROR!!! >.<\nThe description of a todo can't be empty.` |
| `list` | `You have no tasks!` |

## TC7: Adding a deadline
**Aim:** Level-8: `deadline <desc> /by <date>` adds a task tagged `[D]`; the date is parsed from `yyyy-MM-dd` input and displayed as `MMM dd yyyy`, not shown verbatim.

| Input | Expected Output |
|---|---|
| `deadline return book /by 2019-06-06` | `Got it. I've added this task:\n  [D][ ] return book (by: Jun 06 2019)\nNow you have 1 tasks in the list.` |
| `deadline submit report /by 2024-01-01` | `[D][ ] submit report (by: Jan 01 2024)` |

## TC7b: deadline error handling
**Aim:** Level-5/8: a `deadline` missing `/by` entirely, missing its description, missing the due date after `/by`, or given text that isn't a valid `yyyy-MM-dd` date must each be rejected with a distinct message.

| Input | Expected Output |
|---|---|
| `deadline return book` | `ERROR!!! >.<\nA deadline needs a due date!` |
| `deadline /by 2019-06-06` | `ERROR!!! >.<\nThe description of a deadline can't be empty` |
| `deadline return book /by` | `ERROR!!! >.<\nThe due time after` |
| `deadline return book /by tomorrow` | `ERROR!!! >.<\nThe date after `/by` must be in yyyy-MM-dd format` |
| `deadline` | `ERROR!!! >.<\nThe description of a deadline can't be empty` |
| `list` | `You have no tasks!` |

## TC8: Adding an event
**Aim:** Level-8: `event <desc> /from <start-date> /to <end-date>` adds a task tagged `[E]` with both dates parsed from `yyyy-MM-dd` input and displayed as `MMM dd yyyy`.

| Input | Expected Output |
|---|---|
| `event project meeting /from 2019-08-06 /to 2019-08-07` | `Got it. I've added this task:\n  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)\nNow you have 1 tasks in the list.` |

## TC8b: event error handling
**Aim:** Level-5/8: an `event` missing `/from`, missing `/to`, missing its description, missing either date, or given a `/from`/`/to` value that isn't a valid `yyyy-MM-dd` date must each be rejected with a distinct message.

| Input | Expected Output |
|---|---|
| `event project meeting` | `ERROR!!! >.<\nAn event needs a start time!` |
| `event /from 2019-08-06 /to 2019-08-07` | `ERROR!!! >.<\nThe description of an event can't be empty` |
| `event meeting /from 2019-08-06` | `ERROR!!! >.<\nAn event needs an end time.` |
| `event meeting /from /to 2019-08-07` | `ERROR!!! >.<\nThe start time after` |
| `event meeting /from 2019-08-06 /to` | `ERROR!!! >.<\nThe end time after` |
| `event meeting /from tomorrow /to 2019-08-07` | `ERROR!!! >.<\nThe date after `/from` must be in yyyy-MM-dd format` |
| `event meeting /from 2019-08-06 /to whenever` | `ERROR!!! >.<\nThe date after `/to` must be in yyyy-MM-dd format` |
| `event` | `ERROR!!! >.<\nThe description of an event can't be empty` |
| `list` | `You have no tasks!` |

## TC9b: Sub-command markers are case-insensitive
**Aim:** `/by`, `/from`, and `/to` must be recognised regardless of casing (e.g. `/BY`, `/From`, `/TO`).

| Input | Expected Output |
|---|---|
| `deadline return book /BY 2019-06-06` | `[D][ ] return book (by: Jun 06 2019)` |
| `event project meeting /From 2019-08-06 /TO 2019-08-07` | `[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)` |

## TC9: Marking a typed task shows its type tag
**Aim:** Marking/unmarking a `Deadline`/`Event`/`Todo` must still show its `[D]`/`[E]`/`[T]` tag and date info, not just the bare description, since the mark/unmark output is now built polymorphically from the task itself.

| Input | Expected Output |
|---|---|
| `event project meeting /from 2019-08-06 /to 2019-08-07` | `added this task` |
| `mark 1` | `Nice! I've marked this task as done:\n  [E][X] project meeting (from: Aug 06 2019 to: Aug 07 2019)` |
| `unmark 1` | `OK, I've marked this task as not done yet:\n  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)` |

## TC11: `on` lists deadlines/events happening on a given date
**Aim:** Level-8 stretch goal: `on <date>` lists every deadline due, and every event spanning, the given date. A todo never matches since it has no date, and a deadline/event on a different date is excluded.

| Input | Expected Output |
|---|---|
| `deadline return book /by 2019-12-02` | `Got it. I've added this task:` |
| `event conference /from 2019-12-01 /to 2019-12-03` | `Got it. I've added this task:` |
| `todo unrelated task` | `Got it. I've added this task:` |
| `deadline other deadline /by 2019-12-25` | `Got it. I've added this task:` |
| `on 2019-12-02` | `You have 2 tasks on Dec 02 2019!\n1.[D][ ] return book (by: Dec 02 2019)\n2.[E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)` |

## TC11b: `on` with no matching tasks
**Aim:** Level-8 stretch goal: querying a date with nothing due/happening on it should say so, not print an empty list.

| Input | Expected Output |
|---|---|
| `deadline return book /by 2019-12-02` | `Got it. I've added this task:` |
| `on 2020-01-01` | `You have no tasks on Jan 01 2020!` |

## TC11c: `on` error handling
**Aim:** Level-8 stretch goal: `on` with no date, or a date that isn't a valid `yyyy-MM-dd` date, must be rejected like other date inputs.

| Input | Expected Output |
|---|---|
| `on` | `ERROR!!! >.<\nTell me which date to look up!` |
| `on someday` | `ERROR!!! >.<\nThe date after `on` must be in yyyy-MM-dd format` |
