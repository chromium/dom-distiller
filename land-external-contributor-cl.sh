#!/usr/bin/env bash
# A helpful script to land contributions from external developers.
# The script requires one parameter, which is the issue number to land on behalf
# of the author.
if [[ -z "$1" ]]; then
  echo "First argument must be the issue number."
  echo "Example: $(basename $0) 123456"
  exit 1
fi
(
  get_author_from_first_comment() {
    # Need to strip colors first from the result of |git cl comments|.
    git cl comments | \
        sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]//g" | \
        grep "@" | \
        head -n 1 | \
        awk '{print $3;}'
  }

  set -e
  issue_number=$1
  # Ensure origin/master is up to date, and checkout a branch which tracks it.
  git remote update
  git checkout -tb land-issue-${issue_number} origin/master

  # Setup the branch correctly with the last patch from the review.
  git cl patch ${issue_number}
  git cl issue ${issue_number}

  # Print the CL description to help identify if this is the correct CL.
  echo "The following is the description on that issue:"
  echo "######"
  GIT_EDITOR=cat git cl description | \
      grep -v "^#" | \
      sed -r "s/Loaded authentication cookies from.*$//g"
  echo "######"

  # Get author e-mail by inspecting the first comment, and ask user to verify.
  author_email=$(get_author_from_first_comment)
  echo "Author e-mail: Enter to use, or enter correct e-mail [${author_email}]:"
  read email_override
  if [[ ! -z "${email_override}" ]]; then
    author_email="${email_override}"
  fi

  # Get author name from the username in the e-mail, and ask user to verify.
  author_name=$(echo ${author_email} | awk '{split($0,a,"@"); print a[1]};')
  echo "Author name: Enter to use, or enter correct name. [${author_name}]:"
  read name_override
  if [[ ! -z "${name_override}" ]]; then
    author_name="${name_override}"
  fi

  # Land the current branch.
  git cl land -c "${author_name} <${author_email}>"
)
