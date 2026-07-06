# Solution

## Approach

Convert the `wait-for-it.sh` scripts to LF line endings so Linux can execute them correctly.

## Fix applied

- Normalized line endings for:
  - `search/wait-for-it.sh`
  - `storefront-bff/wait-for-it.sh`
  - `backoffice-bff/wait-for-it.sh`
- Kept the script content unchanged except for a small wording update in the comment.
- Rebuilt the `search` image and recreated the container.

## Verification

After the fix:

- `wait-for-it.sh` starts normally and waits for `elasticsearch:9200`
- `search` boots successfully
- `http://api.yas.local/search/v3/api-docs` returns `200 OK`

