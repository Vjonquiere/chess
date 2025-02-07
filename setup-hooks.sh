#!/bin/bash

echo "Setting up Git hooks..."

HOOKS_DIR=".git/hooks"
cp hooks/pre-commit "$HOOKS_DIR/pre-commit"
chmod +x "$HOOKS_DIR/pre-commit"

echo "Git hooks installed!"
