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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.nfc.eclipse.plugin.Activator;
import org.nfc.eclipse.plugin.NdefEditorPart;
import org.nfc.eclipse.plugin.NdefMultiPageEditor;
import org.nfc.eclipse.plugin.model.editing.DefaultRecordEditingSupport;
import org.nfc.eclipse.plugin.model.editing.ExternalTypeRecordEditingSupport;
import org.nfc.eclipse.plugin.model.editing.MimeRecordEditingSupport;
import org.nfc.eclipse.plugin.model.editing.UnknownRecordEditingSupport;
import org.nfc.eclipse.plugin.operation.NdefModelOperation;
import org.nfc.eclipse.plugin.terminal.NdefTerminalListener;
import org.nfc.eclipse.plugin.terminal.NdefTerminalWrapper;
import org.nfc.eclipse.plugin.util.FileDialogUtil;
import org.nfctools.ndef.NdefContext;
import org.nfctools.ndef.NdefMessageEncoder;
import org.nfctools.ndef.NdefOperations;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.auri.AbsoluteUriRecord;
import org.nfctools.ndef.empty.EmptyRecord;
import org.nfctools.ndef.ext.AndroidApplicationRecord;
import org.nfctools.ndef.ext.GenericExternalTypeRecord;
import org.nfctools.ndef.mime.BinaryMimeRecord;
import org.nfctools.ndef.mime.MimeRecord;
import org.nfctools.ndef.mime.TextMimeRecord;
import org.nfctools.ndef.unknown.UnknownRecord;
import org.nfctools.ndef.wkt.handover.records.AlternativeCarrierRecord;
import org.nfctools.ndef.wkt.handover.records.HandoverCarrierRecord;
import org.nfctools.ndef.wkt.handover.records.HandoverRequestRecord;
import org.nfctools.ndef.wkt.handover.records.HandoverSelectRecord;
import org.nfctools.ndef.wkt.records.ActionRecord;
import org.nfctools.ndef.wkt.records.GcActionRecord;
import org.nfctools.ndef.wkt.records.GcDataRecord;
import org.nfctools.ndef.wkt.records.GcTargetRecord;
import org.nfctools.ndef.wkt.records.GenericControlRecord;
import org.nfctools.ndef.wkt.records.SignatureRecord;
import org.nfctools.ndef.wkt.records.SmartPosterRecord;
import org.nfctools.ndef.wkt.records.TextRecord;
import org.nfctools.ndef.wkt.records.UriRecord;


public class NdefRecordModelMenuListener implements IMenuListener, ISelectionChangedListener {
	
	
	
	private NdefRecordType[] rootRecordTypes = NdefRecordType.sort(new NdefRecordType[]{
			NdefRecordType.getType(AbsoluteUriRecord.class),
			NdefRecordType.getType(ActionRecord.class),
			NdefRecordType.getType(AndroidApplicationRecord.class),
			NdefRecordType.getType(GenericExternalTypeRecord.class),
			NdefRecordType.getType(EmptyRecord.class),
			NdefRecordType.getType(GenericControlRecord.class),

			NdefRecordType.getType(BinaryMimeRecord.class),
			NdefRecordType.getType(SmartPosterRecord.class),
			NdefRecordType.getType(TextRecord.class),
			NdefRecordType.getType(UnknownRecord.class),
			NdefRecordType.getType(UriRecord.class),
			
			NdefRecordType.getType(SignatureRecord.class),
	});

	private NdefRecordType[] handoverRecordTypes = NdefRecordType.sort(new NdefRecordType[]{
			NdefRecordType.getType(HandoverSelectRecord.class),
			NdefRecordType.getType(HandoverCarrierRecord.class),
			NdefRecordType.getType(HandoverRequestRecord.class),
	});

	
	public static NdefRecordType[] genericControlRecordTargetRecordTypes = NdefRecordType.sort(new NdefRecordType[]{
			NdefRecordType.getType(TextRecord.class),
			NdefRecordType.getType(UriRecord.class)
	});

	private NdefRecordType[] genericControlRecordDataChildRecordTypes = rootRecordTypes;

	@SuppressWarnings("unused")
	private NdefRecordType[] genericControlRecordActionRecordTypes = rootRecordTypes;

	public static NdefRecordType[] wellKnownRecordTypes = NdefRecordType.sort(new NdefRecordType[]{
			NdefRecordType.getType(ActionRecord.class),
			NdefRecordType.getType(SmartPosterRecord.class),
			NdefRecordType.getType(TextRecord.class),
			NdefRecordType.getType(UriRecord.class),
			
			NdefRecordType.getType(AlternativeCarrierRecord.class),
			NdefRecordType.getType(HandoverSelectRecord.class),
			NdefRecordType.getType(HandoverCarrierRecord.class),
			NdefRecordType.getType(HandoverRequestRecord.class),
			
			NdefRecordType.getType(GenericControlRecord.class),
			
			NdefRecordType.getType(SignatureRecord.class),
			
	});
	
	public static NdefRecordType[] externalRecordTypes = NdefRecordType.sort(new NdefRecordType[]{
		NdefRecordType.getType(AndroidApplicationRecord.class),
		NdefRecordType.getType(GenericExternalTypeRecord.class),
	});


	private TreeViewer treeViewer;
	private MenuManager manager = new MenuManager();
	private NdefEditorPart editorPart;
	private NdefMultiPageEditor ndefMultiPageEditor;

	//private int activeColumn = -1;
	
	private NdefRecordModelParent root;
		
	private NdefRecordModelNode selectedNode;
	
	/*
	private void triggerColumnSelectedColumn(final TreeViewer v) {
		v.getTree().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				
				activeColumn = -1;
						
				int x = 0;
				for (int i = 0; i < v.getTree().getColumnCount(); i++) {
					x += v.getTree().getColumn(i).getWidth();
					if (e.x <= x) {
						activeColumn = i;
						break;
					}
				}

			
			}
		});
		
		
	}
	*/
	
	private MenuManager insertRootSiblingRecordBefore;
	private MenuManager insertRootSiblingRecordAfter;
	private MenuManager addRootChildRecord;
	private Action removeRecord;

	// GenericControlData
	private MenuManager insertGenericControlDataSiblingRecordBefore;
	private MenuManager insertGenericControlDataSiblingRecordAfter;
	private MenuManager addGenericControlDataChildRecord;
	
	// GenericControl Target Record
	private MenuManager setGenericControlTargetRecord;

	// GenericControl Action Record
	private MenuManager setGenericControlActionRecord;

	// GenericControl
	private MenuManager addGenericControlActionRecord;
	private MenuManager addGenericControlDataRecord;
	private MenuManager addGenericControlDataOrActionRecord;

	// Lists
	private Action addListItem;
	private Action insertListItemSiblingBefore;
	private Action insertListItemSiblingAfter;
	private Action removeListItem;

	// HandoverRequestRecord
	private Action insertAlternativeCarrierRecordSiblingRecordBefore;
	private Action insertAlternativeCarrierRecordSiblingRecordAfter;
	private Action addAlternativeCarrierRecordChildRecord;

	// HandoverCarrierRecord Action Record
	private MenuManager setHandoverCarrierExternalType;
	private MenuManager setHandoverCarrierWellKnownType;

	// terminal
	private ReadTerminal readTerminal = new ReadTerminal();
	private WriteTerminal writeTerminal = new WriteTerminal();
	private AutoReadTerminal autoReadTerminal = new AutoReadTerminal();
	private AutoWriteTerminal autoWriteTerminal = new AutoWriteTerminal();
	private FormatTerminal formatTerminal = new FormatTerminal();
	private ReadOnlyTerminal readOnlyTerminal = new ReadOnlyTerminal();
	private DisableTerminal disableTerminals = new DisableTerminal();
	private EnableTerminal enableTerminals = new EnableTerminal();
	
	// mime content
	private SaveContentAction saveContent;
	private ReloadContentAction reloadContent;

	private class WriteTerminal extends Action {
		
		public WriteTerminal() {	
			super("Write");
		}
		
		@Override
		public void run() {
			Activator.info("Export to terminal");
			
			List<Record> ndefContent = ndefMultiPageEditor.getNdefRecords();
			
			NdefOperations ndefOperations = NdefTerminalWrapper.getNdefOperations();

			if(ndefOperations != null) {

				try {
					if(!ndefOperations.isFormatted()) {
						ndefOperations.format(ndefContent.toArray(new Record[ndefContent.size()]));
					} else {
						ndefOperations.writeNdefMessage(ndefContent.toArray(new Record[ndefContent.size()]));
					}
					editorPart.setStatus("Write successful");
				} catch(Exception e) {
					editorPart.setStatus("Write failed: " + e.toString());
				}
			} else {
				editorPart.setStatus("Operation not possible");
			}
		}
	}

	private class ReadTerminal extends Action {

		public ReadTerminal() {
			super("Read");
		}
		
		@Override
		public void run() {
			Activator.info("Import from terminal");
			
			NdefOperations ndefOperations = NdefTerminalWrapper.getNdefOperations();
			
			if(ndefOperations != null) {
				try {
					List<Record> readNdefMessage;
					if(ndefOperations.isFormatted()) {
						readNdefMessage = ndefOperations.readNdefMessage();
					} else {
						readNdefMessage = new ArrayList<Record>();
					}
		
					ndefMultiPageEditor.setNdefContent(readNdefMessage);
					editorPart.setStatus("Read successful");
				} catch(Exception e) {
					editorPart.setStatus("Read failed: " + e.toString());
				}
			} else {
				editorPart.setStatus("Operation not possible");
			}
		}
	}
	
	private class EnableTerminal extends Action {

		public EnableTerminal() {
			super("Enable readers");
		}
		
		@Override
		public void run() {
			Activator.info("Enable terminal");

			NdefTerminalWrapper.setReaderEnabledPreference(true);
			NdefTerminalWrapper.enable();
		}
	}

	private class DisableTerminal extends Action {

		public DisableTerminal() {
			super("Disable readers");
		}
		
		@Override
		public void run() {
			Activator.info("Disable terminal");

			NdefTerminalWrapper.setReaderEnabledPreference(false);
			NdefTerminalWrapper.disable();
		}
	}

	private class AutoWriteTerminal extends Action {
		
		public AutoWriteTerminal() {
			super("Auto-write here", Action.AS_CHECK_BOX);
		}
		
		@Override
		public void run() {
			Activator.info("Automatically export to terminal");
			
			if(isChecked()) {
				NdefTerminalWrapper.setNdefTerminalWriteListener(ndefMultiPageEditor);

				// write now
				NdefOperations ndefOperations = NdefTerminalWrapper.getNdefOperations();

				if(ndefOperations != null) {
					List<Record> ndefContent = ndefMultiPageEditor.getNdefRecords();

					try {
						if(!ndefOperations.isFormatted()) {
							ndefOperations.format(ndefContent.toArray(new Record[ndefContent.size()]));
						} else {
							ndefOperations.writeNdefMessage(ndefContent.toArray(new Record[ndefContent.size()]));
						}
						editorPart.setStatus("Auto-write successful");
					} catch(Exception e) {
						editorPart.setStatus("Auto-write failed: " + e.toString());
					}
				}					
			} else {
				NdefTerminalWrapper.setNdefTerminalWriteListener(null);
			}
			
		}
	}

	private class AutoReadTerminal extends Action {

		public AutoReadTerminal() {
			super("Auto-read here", Action.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			Activator.info("Automatically import from terminal");

			if(isChecked()) {
				NdefTerminalWrapper.setNdefTerminalReadListener(ndefMultiPageEditor);
			} else {
				NdefTerminalWrapper.setNdefTerminalReadListener(null);
			}
		}
	}

	private class FormatTerminal extends Action {
		
		public FormatTerminal() {
			super("Format");
		}
		
		@Override
		public void run() {
			Activator.info("Format");
			
			NdefOperations ndefOperations = NdefTerminalWrapper.getNdefOperations();

			if(ndefOperations != null) {
				try {
					ndefOperations.format();
					
					editorPart.setStatus("Format successful");
				} catch(Exception e) {
					editorPart.setStatus("Format failed: " + e.toString());
				}
			} else {
				editorPart.setStatus("Operation not possible");
			}
		}
		
	}

	
	private class ReadOnlyTerminal extends Action {
		
		public ReadOnlyTerminal() {
			super("Set read-only");
		}
		
		@Override
		public void run() {
			Activator.info("Set to read only");
			
			NdefOperations ndefOperations = NdefTerminalWrapper.getNdefOperations();

			if(ndefOperations != null) {
				try {
					ndefOperations.makeReadOnly();
					
					editorPart.setStatus("Set read-only successful");
				} catch(Exception e) {
					editorPart.setStatus("Set read-only failed: " + e.toString());
				}
			} else {
				editorPart.setStatus("Operation not possible");
			}
		}
		
	}
	
	private class InsertSiblingAction extends Action {

		private Class<? extends Record> recordType;
		private int offset;
		
		public InsertSiblingAction(String name, Class<? extends Record> recordType, int offset) {
			super(name);
			
			this.recordType = recordType;
			this.offset = offset;
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				editorPart.addRecord(selectedNode.getParent(), selectedNode.getParentIndex() + offset, recordType);
			}
		}
	}
	
	private class AddChildAction extends Action {

		private Class<? extends Record> recordType;
		
		public AddChildAction(String name, Class<? extends Record> recordType) {
			super(name);
			
			this.recordType = recordType;
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				if(selectedNode != null) {
					editorPart.addRecord((NdefRecordModelParent)selectedNode, -1, recordType);
				} else {
					editorPart.addRecord((NdefRecordModelParent)root, -1, recordType);
				}
			}
		}
	}
	
	/*
	private class ViewContentAction extends Action {

		public ViewContentAction(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				
				byte[] payload;
				Record record = selectedNode.getRecord();
				if(record instanceof MimeRecord) {
					MimeRecord mimeRecord = (MimeRecord) record;

					payload = mimeRecord.getContentAsBytes();
				} else if(record instanceof UnknownRecord) {
					UnknownRecord unknownRecord = (UnknownRecord) record;
					
					payload = unknownRecord.getPayload();
				} else {
					throw new RuntimeException();
				}

				Activator.info("View " + payload.length + " bytes");
			}
		}
	}
	*/
	private class ReloadContentAction extends Action {

		public ReloadContentAction(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				
		    	Display.getCurrent().asyncExec(
		                new Runnable()
		                {
		                    public void run()
		                    {

		                    	if(selectedNode instanceof NdefRecordModelBinaryProperty) {
		                    		NdefRecordModelBinaryProperty binary = (NdefRecordModelBinaryProperty)selectedNode;
		                    		
			                    	String fileString = binary.getFile();
			                    	
			                    	Activator.info("Reload file " + fileString);
	
			                    	loadBinaryContent(fileString);
		                    	} else {
		                    		throw new RuntimeException();
		                    	}
		                    }
		                }
		            );

			}
		                    
		                    
		}
	}
	
	private class SaveContentAction extends Action {

		private String mimeType;
		
		public SaveContentAction(String name) {
			super(name);
		}
		
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				
		    	Display.getCurrent().asyncExec(
		                new Runnable()
		                {
		                    public void run()
		                    {

				// File standard dialog
				FileDialog fileDialog = new FileDialog(treeViewer.getTree().getShell(), SWT.SAVE);
				// Set the text
				fileDialog.setText("Save mime media");
				// Set filter

				final String fileString = FileDialogUtil.open(fileDialog, SaveContentAction.this.mimeType);
				
				if(fileString != null) {
					Activator.info("Save to file " + fileString);

					saveBinaryContent(fileString);
				} else {
					Activator.info("No save");
				}
				
		                    }
		                }
		            );

			}
		}
	}

	private void loadBinaryContent(final String fileString) {
		
		byte[] payload = DefaultRecordEditingSupport.load(fileString);
		if(payload != null) {
			NdefModelOperation operation = null;
			Record record = selectedNode.getRecord();
			if(record instanceof BinaryMimeRecord) {
				BinaryMimeRecord mimeRecord = (BinaryMimeRecord) record;

				operation = MimeRecordEditingSupport.newSetContentOperation(mimeRecord, (NdefRecordModelProperty) selectedNode, payload);
			} else if(record instanceof UnknownRecord) {
				UnknownRecord unknownRecord = (UnknownRecord) record;
				
				operation = UnknownRecordEditingSupport.newSetContentOperation(unknownRecord, (NdefRecordModelProperty) selectedNode, payload);
			} else if(record instanceof GenericExternalTypeRecord) {
				GenericExternalTypeRecord unsupportedExternalTypeRecord = (GenericExternalTypeRecord) record;
				
				operation = ExternalTypeRecordEditingSupport.newSetContentOperation(unsupportedExternalTypeRecord, (NdefRecordModelProperty) selectedNode, payload);
			} else if(record instanceof SignatureRecord) {
				
				// TODO add reload capability
				if(selectedNode.getRecordBranchIndex() == 2) {
					SignatureRecord signatureRecord = (SignatureRecord)record;
					
					if(signatureRecord.hasSignature()) {
						payload = signatureRecord.getSignature();
					}
				} else if(selectedNode.getRecordBranchIndex() == 4) {
					if(selectedNode instanceof NdefRecordModelPropertyListItem) {
						SignatureRecord signatureRecord = (SignatureRecord)record;
						
						payload = signatureRecord.getCertificates().get(selectedNode.getParentIndex());
					}
				}
			}

			if(operation != null) {
				editorPart.update(selectedNode, operation);
			}
		}
	}
	

	
	private void saveBinaryContent(final String fileString) {
		File file = new File(fileString);
		
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);

			byte[] payload = null;
			Record record = selectedNode.getRecord();
			if(record instanceof MimeRecord) {
				MimeRecord mimeRecord = (MimeRecord) record;
				
				payload = mimeRecord.getContentAsBytes();
			} else if(record instanceof UnknownRecord) {
				UnknownRecord unknownRecord = (UnknownRecord) record;
				
				payload = unknownRecord.getPayload();
			} else if(record instanceof GenericExternalTypeRecord) {
				GenericExternalTypeRecord unsupportedExternalTypeRecord = (GenericExternalTypeRecord) record;
				
				payload = unsupportedExternalTypeRecord.getData();
			} else if(record instanceof SignatureRecord) {

				if(selectedNode.getRecordBranchIndex() == 2) {
					SignatureRecord signatureRecord = (SignatureRecord)record;
					
					if(signatureRecord.hasSignature()) {
						payload = signatureRecord.getSignature();
					}
				} else if(selectedNode.getRecordBranchIndex() == 4) {
					if(selectedNode instanceof NdefRecordModelPropertyListItem) {
						SignatureRecord signatureRecord = (SignatureRecord)record;
						
						payload = signatureRecord.getCertificates().get(selectedNode.getParentIndex());
					}
				}
			}

			if(payload == null) {
				throw new RuntimeException();
			}
			
			Activator.info("Save " + payload.length + " bytes");

			outputStream.write(payload);
		} catch(IOException e) {
			
			Activator.warn("Unable to save to file " + fileString, e);
			
			// http://www.vogella.de/articles/EclipseDialogs/article.html#dialogs_jfacemessage
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, "Error", "Unable to save to file " + fileString);
		} finally {
			try {
				if(outputStream != null) {
					outputStream.close();
				}
			} catch(Exception e) {
				// ignore
			}
		}
	}
	
	private class SetChildAction extends Action {

		private Class<? extends Record> recordType;
		
		public SetChildAction(String name, Class<? extends Record> recordType) {
			super(name);
			
			this.recordType = recordType;
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				editorPart.setRecord((NdefRecordModelParentProperty)selectedNode, recordType);
			}
		}
	}

	
	private class InsertListItemAction extends Action {

		private int offset;
		
		public InsertListItemAction(String name, int offset) {
			super(name);
			
			this.offset = offset;
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				editorPart.addListItem(selectedNode.getParent(), selectedNode.getParent().indexOf(selectedNode) + offset);
			}
		}
	}
	
	private class AddListItemAction extends Action {

		public AddListItemAction(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				editorPart.addListItem((NdefRecordModelParent)selectedNode, -1);
			}
		}
	}

	private class RemoveAction extends Action {

		public RemoveAction(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				if(selectedNode != null) {
					editorPart.removeRecord((NdefRecordModelRecord)selectedNode);
					
					selectedNode = null;
				}
			}
		}

	}
	
	private class RemoveListItemAction extends Action {

		public RemoveListItemAction(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			if(editorPart != null) {
				if(selectedNode != null) {
					editorPart.removeListItem((NdefRecordModelPropertyListItem)selectedNode);
					
					selectedNode = null;
				}
			}
		}

	}

	public NdefRecordModelMenuListener(final TreeViewer treeViewer, final NdefEditorPart ndefEditorPart, NdefMultiPageEditor ndefMultiPageEditor, NdefRecordModelParent root) {
		this.treeViewer = treeViewer;
		this.editorPart = ndefEditorPart;
		this.ndefMultiPageEditor = ndefMultiPageEditor;
		this.root = root;
		
		initializeRootAddInsert();
		
		// generic control
		// insert before
		insertGenericControlDataSiblingRecordBefore = new MenuManager("Insert record before", null);
        
        for(NdefRecordType recordType: genericControlRecordDataChildRecordTypes) {
        	insertGenericControlDataSiblingRecordBefore.add(new InsertSiblingAction(recordType.getRecordLabel(), recordType.getRecordClass(), 0));
        }
		
		// insert after
        insertGenericControlDataSiblingRecordAfter = new MenuManager("Insert record after", null);
        
        for(NdefRecordType recordType: genericControlRecordDataChildRecordTypes) {
        	insertGenericControlDataSiblingRecordAfter.add(new InsertSiblingAction(recordType.getRecordLabel(), recordType.getRecordClass(), 1));
        }

		// just insert
        addGenericControlDataChildRecord = new MenuManager("Add record", null);
        
        for(NdefRecordType recordType: genericControlRecordDataChildRecordTypes) {
        	addGenericControlDataChildRecord.add(new AddChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }

        addGenericControlActionRecord = new MenuManager("Add record", null);
       	addGenericControlActionRecord.add(new AddChildAction(NdefRecordType.getType(GcActionRecord.class).getRecordLabel(), GcActionRecord.class));

        addGenericControlDataRecord = new MenuManager("Add record", null);
       	addGenericControlDataRecord.add(new AddChildAction(NdefRecordType.getType(GcDataRecord.class).getRecordLabel(), GcDataRecord.class));

        addGenericControlDataOrActionRecord = new MenuManager("Add record", null);
        addGenericControlDataOrActionRecord.add(new AddChildAction(NdefRecordType.getType(GcActionRecord.class).getRecordLabel(), GcActionRecord.class));
        addGenericControlDataOrActionRecord.add(new AddChildAction(NdefRecordType.getType(GcDataRecord.class).getRecordLabel(), GcDataRecord.class));

		removeRecord = new RemoveAction("Remove record");

		setGenericControlTargetRecord = new MenuManager("Set target identifier", null);
        for(NdefRecordType recordType: genericControlRecordTargetRecordTypes) {
        	setGenericControlTargetRecord.add(new SetChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
        
        setGenericControlActionRecord = new MenuManager("Set action record", null);
        for(NdefRecordType recordType: rootRecordTypes) {
        	setGenericControlActionRecord.add(new SetChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
        MenuManager setGenericControlActionRecordHandoverRecords = new MenuManager("Handover records", null);
        for(NdefRecordType recordType: handoverRecordTypes) {
        	setGenericControlActionRecordHandoverRecords.add(new SetChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
        setGenericControlActionRecord.add(setGenericControlActionRecordHandoverRecords);
                
        // HandoverRequestRecord
    	insertAlternativeCarrierRecordSiblingRecordBefore = new InsertSiblingAction("Insert " + NdefRecordType.getType(AlternativeCarrierRecord.class).getRecordLabel() + " before", AlternativeCarrierRecord.class, 0);
    	insertAlternativeCarrierRecordSiblingRecordAfter = new InsertSiblingAction("Insert " + NdefRecordType.getType(AlternativeCarrierRecord.class).getRecordLabel() + " after", AlternativeCarrierRecord.class, 1);
    	addAlternativeCarrierRecordChildRecord = new AddChildAction("Add " + NdefRecordType.getType(AlternativeCarrierRecord.class).getRecordLabel(), AlternativeCarrierRecord.class);

    	// HandoverCarrierRecord
    	// well known type
        setHandoverCarrierWellKnownType = new MenuManager("Set carrier type", null);
        for(NdefRecordType recordType: wellKnownRecordTypes) {
        	setHandoverCarrierWellKnownType.add(new SetChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
        
        // external type
        setHandoverCarrierExternalType = new MenuManager("Set carrier type", null);
        for(NdefRecordType recordType: externalRecordTypes) {
        	setHandoverCarrierExternalType.add(new SetChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
    	
        // list
        addListItem = new AddListItemAction("Add item");
        insertListItemSiblingBefore = new InsertListItemAction("Insert item before", 0);
        insertListItemSiblingAfter = new InsertListItemAction("Insert item after", 1);
        removeListItem = new RemoveListItemAction("Remove item");
        
        // mime interaction
        saveContent = new SaveContentAction("Save to file");
        reloadContent = new ReloadContentAction("Reload previous file");
        
		manager.setRemoveAllWhenShown(true);
		
		manager.addMenuListener(this);
		
		treeViewer.getControl().setMenu(manager.createContextMenu(treeViewer.getControl()));
		
		//triggerColumnSelectedColumn(treeViewer);
		
		treeViewer.addSelectionChangedListener(this);
	}

	private void initializeRootAddInsert() {
		// root
		// insert before
		insertRootSiblingRecordBefore = new MenuManager("Insert record before", null);
        
        for(NdefRecordType recordType: rootRecordTypes) {
        	insertRootSiblingRecordBefore.add(new InsertSiblingAction(recordType.getRecordLabel(), recordType.getRecordClass(), 0));
        }

        String handoverRecords = "Handover records";
        MenuManager insertRootSiblingRecordBeforeHandoverRecords = new MenuManager(handoverRecords, null);
        for(NdefRecordType recordType: handoverRecordTypes) {
        	insertRootSiblingRecordBeforeHandoverRecords.add(new InsertSiblingAction(recordType.getRecordLabel(), recordType.getRecordClass(), 0));
        }
        int index = -1;
        for(int i = 0; i < rootRecordTypes.length; i++) {
        	if(rootRecordTypes[i].getRecordLabel().compareTo(handoverRecords) > 0) {
        		index = i;
        		
        		break;
        	}
        }
        if(index == -1) {
            insertRootSiblingRecordBefore.add(insertRootSiblingRecordBeforeHandoverRecords);
        } else {
            insertRootSiblingRecordBefore.insert(index, insertRootSiblingRecordBeforeHandoverRecords);
        }
        
		// insert after
        insertRootSiblingRecordAfter = new MenuManager("Insert record after", null);
        for(NdefRecordType recordType: rootRecordTypes) {
        	insertRootSiblingRecordAfter.add(new InsertSiblingAction(recordType.getRecordLabel(), recordType.getRecordClass(), 1));
        }

        MenuManager insertRootSiblingRecordAfterHandoverRecords = new MenuManager(handoverRecords, null);
        for(NdefRecordType recordType: handoverRecordTypes) {
        	insertRootSiblingRecordAfterHandoverRecords.add(new InsertSiblingAction(recordType.getRecordLabel(), recordType.getRecordClass(), 1));
        }
        if(index == -1) {
        	insertRootSiblingRecordAfter.add(insertRootSiblingRecordAfterHandoverRecords);
        } else {
        	insertRootSiblingRecordAfter.insert(index, insertRootSiblingRecordAfterHandoverRecords);
        }
        
		// just add as in add last
        addRootChildRecord = new MenuManager("Add record", null);
        for(NdefRecordType recordType: rootRecordTypes) {
        	addRootChildRecord.add(new AddChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
        MenuManager addRootChildRecordHandoverRecords = new MenuManager("Handover records", null);
        for(NdefRecordType recordType: handoverRecordTypes) {
        	addRootChildRecordHandoverRecords.add(new AddChildAction(recordType.getRecordLabel(), recordType.getRecordClass()));
        }
        if(index == -1) {
        	addRootChildRecord.add(addRootChildRecordHandoverRecords);
        } else {
        	addRootChildRecord.insert(index, addRootChildRecordHandoverRecords);
        }
	}
	
	@Override
	public void menuAboutToShow(IMenuManager menuManager) {
		
		if(selectedNode != null) {

			Record parentRecord = selectedNode.getParentRecord();

			// filter out list types
			if(selectedNode instanceof NdefRecordModelPropertyListItem) {
				menuManager.add(insertListItemSiblingBefore);
				menuManager.add(insertListItemSiblingAfter);
				menuManager.add(removeListItem);

				if(parentRecord instanceof SignatureRecord) {
					if(selectedNode.getRecordBranchIndex() == 4) {
						saveContent.setMimeType(null);
						menuManager.add(saveContent);
					}
				}
			} else if(selectedNode instanceof NdefRecordModelPropertyList) {
				menuManager.add(addListItem);
			} else {
			
				// parent operation (sibling) options
				if(parentRecord == null) {
					// root
					// add and remove sibling nodes
					menuManager.add(insertRootSiblingRecordBefore);
					menuManager.add(insertRootSiblingRecordAfter);
					menuManager.add(removeRecord);
				} else {
					
					if(selectedNode instanceof NdefRecordModelRecord) {
						// parent operation options
						if(parentRecord instanceof GcDataRecord) {
							// add and remove sibling nodes
							menuManager.add(insertGenericControlDataSiblingRecordBefore);
							menuManager.add(insertGenericControlDataSiblingRecordAfter);
							menuManager.add(removeRecord);
						} else if(parentRecord instanceof GcTargetRecord) {
							menuManager.add(removeRecord);
						} else if(parentRecord instanceof GcActionRecord) {
							menuManager.add(removeRecord);
						} else if(parentRecord instanceof HandoverRequestRecord) {
							if(selectedNode.getRecordBranchIndex() == 3) {
								menuManager.add(insertAlternativeCarrierRecordSiblingRecordBefore);
								menuManager.add(insertAlternativeCarrierRecordSiblingRecordAfter);
								menuManager.add(removeRecord);
							}
						} else if(parentRecord instanceof HandoverSelectRecord) {
							if(selectedNode.getRecordBranchIndex() == 2) {
								menuManager.add(insertAlternativeCarrierRecordSiblingRecordBefore);
								menuManager.add(insertAlternativeCarrierRecordSiblingRecordAfter);
								menuManager.add(removeRecord);
							}
						}
					}
					
					// child operation options
					Record record = selectedNode.getRecord();
					
					if(record instanceof GcDataRecord) {
						menuManager.add(addGenericControlDataChildRecord);
					} else if(record instanceof GcTargetRecord) {
						if(selectedNode instanceof NdefRecordModelParentProperty) {
							menuManager.add(setGenericControlTargetRecord);
						}
					} else if(record instanceof GcActionRecord) {
						if(selectedNode instanceof NdefRecordModelParentProperty) {
							menuManager.add(setGenericControlActionRecord);
						}
					} else if(record instanceof GenericControlRecord) {
						GenericControlRecord genericControlRecord = (GenericControlRecord)record;
						
						if(!genericControlRecord.hasAction() && !genericControlRecord.hasData()) {
							menuManager.add(addGenericControlDataOrActionRecord);
						} else if(!genericControlRecord.hasAction()) {
							menuManager.add(addGenericControlActionRecord);
						} else if(!genericControlRecord.hasData()) {
							menuManager.add(addGenericControlDataRecord);
						}
					} else if(record instanceof HandoverRequestRecord) {
						if(selectedNode instanceof NdefRecordModelParentProperty) {
							menuManager.add(addAlternativeCarrierRecordChildRecord);
						}
					} else if(record instanceof HandoverSelectRecord) {
						
						if(selectedNode instanceof NdefRecordModelParentProperty) {
							if(selectedNode.getRecordBranchIndex() == 2) {
								menuManager.add(addAlternativeCarrierRecordChildRecord);
							}
						}
					} else if(record instanceof HandoverCarrierRecord) {

						if(selectedNode instanceof NdefRecordModelParentProperty) {
							if(selectedNode.getRecordBranchIndex() == 1) {
								HandoverCarrierRecord handoverCarrierRecord = (HandoverCarrierRecord)record;
							
								if(handoverCarrierRecord.hasCarrierTypeFormat()) {
									HandoverCarrierRecord.CarrierTypeFormat carrierTypeFormat = handoverCarrierRecord.getCarrierTypeFormat();
								
									switch(carrierTypeFormat) {
										case WellKnown : {
											// NFC Forum well-known type [NFC RTD]
											menuManager.add(setHandoverCarrierWellKnownType);
											break;
										}
										
										case External : {
											// NFC Forum external type [NFC RTD]
											menuManager.add(setHandoverCarrierWellKnownType);
											break;
										}
									}
								}
							}
						}
					} else if(record instanceof MimeRecord) {

						if(selectedNode.getRecordBranchIndex() == 1) {
							MimeRecord mimeRecord = (MimeRecord)record;
							
							boolean hasContent = false;
							if(mimeRecord instanceof BinaryMimeRecord) {
								BinaryMimeRecord binaryMimeRecord = (BinaryMimeRecord)mimeRecord;
								
								byte[] content = binaryMimeRecord.getContent();
								
								if(content != null && content.length > 0) {
									hasContent = true;
								}
							} else if(mimeRecord instanceof TextMimeRecord) {
								TextMimeRecord textMimeRecord = (TextMimeRecord)mimeRecord;
								
								String content = textMimeRecord.getContent();
								
								if(content != null && content.length() > 0) {
									hasContent = true;
								}
							} else {
								throw new IllegalArgumentException();
							}
							
							if(hasContent) {
								//menuManager.add(viewContent);
								saveContent.setMimeType(mimeRecord.getContentType());
								menuManager.add(saveContent);
							}
						}
					} else if(record instanceof GenericExternalTypeRecord) {

						if(selectedNode.getRecordBranchIndex() == 2) {
							GenericExternalTypeRecord mimeRecord = (GenericExternalTypeRecord)record;
							if(mimeRecord.hasData()) {
								//menuManager.add(viewContent);
								saveContent.setMimeType(null);
								menuManager.add(saveContent);
							}
						}
					} else if(record instanceof UnknownRecord) {

						UnknownRecord unknownRecord = (UnknownRecord)record;
						if(unknownRecord.hasPayload()) {
							//menuManager.add(viewContent);
							saveContent.setMimeType(null);
							menuManager.add(saveContent);
						}
					} else if(record instanceof SignatureRecord) {

						if(selectedNode.getRecordBranchIndex() == 2 && selectedNode instanceof NdefRecordModelProperty) {
							SignatureRecord signatureRecord = (SignatureRecord)record;
							
							if(signatureRecord.hasSignature()) {
								//menuManager.add(viewContent);
								saveContent.setMimeType(null);
								menuManager.add(saveContent);
							}

						}
					} 
					
					if(selectedNode instanceof NdefRecordModelBinaryProperty) {
						NdefRecordModelBinaryProperty ndefRecordModelBinaryProperty = (NdefRecordModelBinaryProperty)selectedNode;
						
						if(ndefRecordModelBinaryProperty.hasFile()) {
							menuManager.add(reloadContent);
						}
					}
				}
			}
		} else {
			// force select of root node
			menuManager.add(addRootChildRecord);
		}
		
			if(NdefTerminalWrapper.isAvailable()) {
				if(NdefTerminalWrapper.isReaderEnabledPreference()) {

					String terminalName = NdefTerminalWrapper.getTerminalName();
				
					if(terminalName != null) {
				        MenuManager terminalMenuManager = new MenuManager(terminalName, null);
				        
				        NdefOperations ndefOperations = NdefTerminalWrapper.getNdefOperations();
		
				        terminalMenuManager.add(readTerminal);
				        terminalMenuManager.add(writeTerminal);
				        
				        if(ndefOperations != null) {
				        	readTerminal.setEnabled(true);
				        	if(ndefOperations.isWritable()) {
		
				        		// add write option IF message can in fact be written
				        		NdefMessageEncoder ndefMessageEncoder = NdefContext.getNdefMessageEncoder();
				        		
				        		try {
				        			ndefMessageEncoder.encode(ndefMultiPageEditor.getNdefRecords());
				        			
				        			writeTerminal.setEnabled(true);
				        		} catch(Exception e) {
				        			writeTerminal.setEnabled(false);
				        		}
				        	}
				        } else {
				        	readTerminal.setEnabled(false);
		        			writeTerminal.setEnabled(false);
				        }
				        
				        NdefTerminalListener read = NdefTerminalWrapper.getNdefTerminalReadListener();
				        if(read != null) {
				        	if(read != ndefMultiPageEditor) {
				        		autoReadTerminal.setChecked(false);
				        	} else {
				        		autoReadTerminal.setChecked(true);
				        	}
				        } else {
			        		autoReadTerminal.setChecked(false);
				        }

				        NdefTerminalListener write = NdefTerminalWrapper.getNdefTerminalWriteListener();
				        if(write != null) {
				        	if(write != ndefMultiPageEditor) {
				        		autoWriteTerminal.setChecked(false);
				        	} else {
				        		autoWriteTerminal.setChecked(true);
				        	}
				        } else {
			        		autoWriteTerminal.setChecked(false);
				        }

				        // always present
				        terminalMenuManager.add(autoReadTerminal);
				        terminalMenuManager.add(autoWriteTerminal);
		
				        if(ndefOperations != null) {
				        	if(ndefOperations.isWritable()) {
				        		formatTerminal.setEnabled(true);
				        		readOnlyTerminal.setEnabled(true);
				        	} else {
				        		formatTerminal.setEnabled(false);
				        		readOnlyTerminal.setEnabled(false);
				        	}
			        	} else {
			        		formatTerminal.setEnabled(false);
			        		readOnlyTerminal.setEnabled(false);
				        }
		
				        terminalMenuManager.add(new Separator());
				        terminalMenuManager.add(formatTerminal);
				        terminalMenuManager.add(new Separator());
				        terminalMenuManager.add(readOnlyTerminal);
				        
				        terminalMenuManager.add(new Separator());
				        terminalMenuManager.add(disableTerminals);

				        menuManager.add(new Separator());
				        menuManager.add(terminalMenuManager);
					} else {
						if(NdefTerminalWrapper.hasSeenReader()) {
							menuManager.add(new Separator());
							menuManager.add(disableTerminals);
						} else {
							// dont show anything
						}
					}
				} else {
					 menuManager.add(new Separator());
				     menuManager.add(enableTerminals);
				}
			}
		
			
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection iStructuredSelection = (IStructuredSelection)event.getSelection();
        
        if(iStructuredSelection != null) {
        	this.selectedNode = (NdefRecordModelNode) iStructuredSelection.getFirstElement();
        } else {
        	this.selectedNode = null;
        }
	}

}
