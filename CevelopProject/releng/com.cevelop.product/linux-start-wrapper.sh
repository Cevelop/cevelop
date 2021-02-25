#!/bin/sh

SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

# The desktop file can only contain absolute image paths:
[ -w "$SCRIPTPATH/cevelop.desktop" ] && sed -i -e "s,Icon=.*,Icon=$SCRIPTPATH/icon.xpm,g" "$SCRIPTPATH/cevelop.desktop"

# Disable the intro, it requires additional system library (webkit gtk) we don't control
export CEVELOP_INTRO=false

# To disable GTK3 (and go back to GTK2), uncomment the following line:
# export SWT_GTK3=0

exec "$SCRIPTPATH/cevelop" "$@"