package com.antares.nfc.plugin.operation;

import org.nfctools.ndef.Record;

import com.antares.nfc.model.NdefRecordModelFactory;
import com.antares.nfc.model.NdefRecordModelParent;
import com.antares.nfc.model.NdefRecordModelParentProperty;
import com.antares.nfc.model.NdefRecordModelRecord;
import com.antares.nfc.plugin.NdefRecordFactory;

public class DefaultNdefRecordModelParentPropertyOperation<V extends Record, R extends Record> implements NdefModelOperation {

	protected NdefRecordModelParentProperty ndefRecordModelParentProperty;

	protected V previous;
	
	protected V next;
	
	protected R record;
	protected NdefRecordModelRecord recordNode;

	protected NdefRecordModelRecord ndefRecordModelRecord;

	public DefaultNdefRecordModelParentPropertyOperation(R record, NdefRecordModelParentProperty ndefRecordModelParentProperty, V previous, V next) {
		this.record = record;
		this.ndefRecordModelParentProperty = ndefRecordModelParentProperty;
		this.previous = previous;
		this.next = next;
		
		initialize();
	}
	
	public void initialize() {
		if(ndefRecordModelParentProperty.hasChildren()) {
			ndefRecordModelRecord = (NdefRecordModelRecord) ndefRecordModelParentProperty.getChild(0);
		}
		
		recordNode = NdefRecordModelFactory.getNode(next, ndefRecordModelParentProperty);
	}							
	
	@Override
	public void execute() {
		if(previous != null) {
			NdefRecordFactory.disconnect(record, previous);
		}
		
		ndefRecordModelParentProperty.removeAllChildren();
		
		NdefRecordFactory.connect(record, next);
		
		ndefRecordModelParentProperty.add(recordNode);

	}
	
	@Override
	public void revoke() {
		NdefRecordFactory.disconnect(record, next);
		ndefRecordModelParentProperty.removeAllChildren();
		
		if(previous != null) {
			NdefRecordFactory.connect(record, previous);
		}
		
		if(ndefRecordModelRecord != null) {
			ndefRecordModelParentProperty.add(ndefRecordModelRecord);
		}
		
	}
	
}