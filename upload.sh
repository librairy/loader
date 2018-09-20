#!/usr/bin/env bash
curl -T $1 -u $3 https://delicias.dia.fi.upm.es/nextcloud/remote.php/webdav/$2
