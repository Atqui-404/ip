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
* You do not need to add a final `bye` row — the test runner appends one
  automatically to end the session cleanly if the last row isn't already
  `bye`.
* TC ids don't need to be numeric or sequential; keep them short and unique.

## Test Cases

## TC1: Listing tasks when none have been added
**Aim:** `list` should tell the user they have no tasks, instead of printing an empty list.

| Input | Expected Output |
|---|---|
| `list` | `You have no tasks!` |

## TC2: Adding a task and seeing it in the list
**Aim:** Any input that isn't a recognised command is stored as a new task and echoed back, then shows up in `list` as not done.

| Input | Expected Output |
|---|---|
| `read book` | `added: read book` |
| `list` | `You have 1 tasks!\n1.[ ] read book` |

## TC3: Marking and unmarking a task
**Aim:** `mark <n>` flips a task to done (`[X]`), and `unmark <n>` flips it back to not done (`[ ]`).

| Input | Expected Output |
|---|---|
| `read book` | `added: read book` |
| `mark 1` | `Nice! I've marked this task as done:\n  [X] read book` |
| `unmark 1` | `OK, I've marked this task as not done yet:\n  [ ] read book` |

## TC4: Marking an out-of-range task index
**Aim:** `mark` on an index that doesn't exist yet should report an error instead of crashing.

| Input | Expected Output |
|---|---|
| `mark 5` | `ERROR: Please input a valid index to mark as done` |

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

## TC7: Adding a deadline
**Aim:** `deadline <desc> /by <when>` adds a task tagged `[D]` with the `by` text shown verbatim (no date parsing at this stage).

| Input | Expected Output |
|---|---|
| `deadline return book /by Sunday` | `Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 1 tasks in the list.` |
| `deadline do homework /by no idea :-p` | `[D][ ] do homework (by: no idea :-p)` |

## TC8: Adding an event
**Aim:** `event <desc> /from <start> /to <end>` adds a task tagged `[E]` with both the start and end text shown verbatim.

| Input | Expected Output |
|---|---|
| `event project meeting /from Mon 2pm /to 4pm` | `Got it. I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 1 tasks in the list.` |

## TC9: Marking a typed task shows its type tag
**Aim:** Marking/unmarking a `Deadline`/`Event`/`Todo` must still show its `[D]`/`[E]`/`[T]` tag and date info, not just the bare description, since the mark/unmark output is now built polymorphically from the task itself.

| Input | Expected Output |
|---|---|
| `event project meeting /from Mon 2pm /to 4pm` | `added this task` |
| `mark 1` | `Nice! I've marked this task as done:\n  [E][X] project meeting (from: Mon 2pm to: 4pm)` |
| `unmark 1` | `OK, I've marked this task as not done yet:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)` |
