#!/usr/bin/env bash
# $1 remote file path
# $2 credentials as user:password
curl -u $2 -X MKCOL https://delicias.dia.fi.upm.es/nextcloud/remote.php/webdav/$1