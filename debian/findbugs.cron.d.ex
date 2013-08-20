#
# Regular cron jobs for the findbugs package
#
0 4	* * *	root	[ -x /usr/bin/findbugs_maintenance ] && /usr/bin/findbugs_maintenance
