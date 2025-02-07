#!/bin/bash

if [ ! -f .git/hooks/pre-commit ]; then
    echo "Setting up Git hooks..."

    HOOKS_DIR=".git/hooks"
    cp hooks/pre-commit "$HOOKS_DIR/pre-commit"
    chmod +x "$HOOKS_DIR/pre-commit"

    echo "Git hooks installed!"
else
    echo "Pre-commit hook already set up."
fi
