#!/bin/bash
sed "s/tagVersion/$1/g" deployment-template.yaml  > deployment.yaml