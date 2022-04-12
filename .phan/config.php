<?php

$cfg = require __DIR__ . '/../vendor/mediawiki/mediawiki-phan-config/src/config.php';

$cfg['directory_list'][] = 'php';

$cfg['directory_list'][] = 'vendor/';
$cfg['exclude_analysis_directory_list'][] = 'vendor/';

$cfg['suppress_issue_types'][] = 'PhanAccessMethodPrivate';
$cfg['suppress_issue_types'][] = 'PhanTypeMismatchArgument';
$cfg['suppress_issue_types'][] = 'PhanTypeMismatchProperty';
$cfg['suppress_issue_types'][] = 'PhanTypeMismatchPropertyProbablyReal';

return $cfg;
