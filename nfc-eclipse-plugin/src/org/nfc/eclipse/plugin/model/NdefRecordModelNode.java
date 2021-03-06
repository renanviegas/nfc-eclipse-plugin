/***************************************************************************
 *
 * This file is part of the NFC Eclipse Plugin project at
 * http://code.google.com/p/nfc-eclipse-plugin/
 *
 * Copyright (C) 2012 by Thomas Rorvik Skjolberg.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ****************************************************************************/

package org.nfc.eclipse.plugin.model;

import org.nfctools.ndef.Record;

public class NdefRecordModelNode implements Cloneable {
	
	protected NdefRecordModelParent parent;

	public NdefRecordModelNode() {
	}
	
	public NdefRecordModelNode(NdefRecordModelParent parent) {
		this.parent = parent;
	}

	public NdefRecordModelParent getParent() {
		return parent;
	}

	public void setParent(NdefRecordModelParent parent) {
		this.parent = parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public int getLevel() {
		return getLevel(0);
	}
	
	protected int getLevel(int current) {
		if(parent != null) {
			return parent.getLevel(current + 1);
		}
		return current;
	}
	
	protected int getTreeRootIndex() {
		NdefRecordModelNode p = this;
		
		while(p != null && p.hasParent()) {
			NdefRecordModelParent next = p.getParent();
			
			if(!next.hasParent()) { // next is root
				return next.indexOf(p);
			}
			
			p = next;
		}
		
		return -1;
	}

	/**
	 * 
	 * Get the record to which the first parent of this node belongs
	 * 
	 * @return
	 */

	public Record getParentRecord() {
		if(parent != null) {
			return getRecord(parent);
		}
		return null;
	}
	
	/**
	 * 
	 * Get the record to which this node belongs
	 * 
	 * @return
	 */
	
	public Record getRecord() {
		return getRecord(this);
	}
	
	protected Record getRecord(NdefRecordModelNode p) {
		do {
			if(p instanceof NdefRecordModelRecord) {
				NdefRecordModelRecord ndefRecordModelRecord = (NdefRecordModelRecord)p;
				
				return ndefRecordModelRecord.getRecord();
			}
			
			p = p.getParent();
		} while(p != null && p.hasParent());
		
		return null;
	}
	
	public int getRecordBranchIndex() {
		NdefRecordModelNode p = this;
		
		do {
			if(p.getParent() instanceof NdefRecordModelRecord) {
				return p.getParentIndex();
			}
			
			p = p.getParent();
		} while(p.hasParent());
		
		return -1;
	}

	public int getParentIndex() {
		return parent.indexOf(this);
	}

	public int getRecordLevel() {
		return getRecordLevel(0);
	}
	
	public int getRecordLevel(int level) {
		if(parent != null) {
			if(parent instanceof NdefRecordModelRecord) {
				return level + 1;
			}
			return parent.getRecordLevel(level + 1);
		}
		
		return -1;
	}

	public NdefRecordModelRecord getRecordNode() {
		NdefRecordModelNode p = this;
		
		do {
			if(p instanceof NdefRecordModelRecord) {
				return (NdefRecordModelRecord) p;
			}
			
			p = p.getParent();
		} while(p.hasParent());
		
		return null;
	}

}
