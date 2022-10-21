type CallQueueEntry =
	["submit", string, string, BaseEventData]
	| ["dispatch", string, string, FormattedCustomData]
	;

type CallQueue = CallQueueEntry[];
