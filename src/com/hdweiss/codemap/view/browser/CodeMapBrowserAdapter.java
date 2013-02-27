package com.hdweiss.codemap.view.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hdweiss.codemap.controller.CodeMapController;

public class CodeMapBrowserAdapter extends ArrayAdapter<CodeMapBrowserItem> {

	private ArrayList<Boolean> expanded = new ArrayList<Boolean>();
	private CodeMapController controller;
	private File projectDirectory;
	
	public CodeMapBrowserAdapter(Context context, CodeMapController controller) {
		super(context, android.R.layout.simple_list_item_1);
		this.controller = controller;
		this.projectDirectory = controller.project.getSourceDirectory(getContext());
		init();
	}

	public void init() {
		clear();
		
		String[] fileList = projectDirectory.list();
		
		if (fileList == null)
			return;
		
		List<String> filelist = Arrays.asList(fileList);
		Collections.sort(filelist);
		
		for(String name: filelist)
			add(new CodeMapBrowserItem(name, 0, CodeMapBrowserItem.TYPE.FILE));
				
		notifyDataSetInvalidated();
	}


//	public long[] getState() {
//		int count = getCount();
//		long[] state = new long[count];
//
//		for(int i = 0; i < count; i++)
//			state[i] = getItem(i).id;
//
//		return state;
//	}
//
//	public void setState(long[] state) {
//		clear();
//
//		for(int i = 0; i < state.length; i++) {
//			try {
//				OrgNode node = new OrgNode(state[i], resolver);
//				add(node);
//			} catch (OrgNodeNotFoundException e) {}
//		}
//	}

	public void refresh() {
		ArrayList<Long> expandedNodeIds = new ArrayList<Long>();
		int size = this.expanded.size();
		for(int i = 0; i < size; i++) {
			if(this.expanded.get(i))
				expandedNodeIds.add(getItemId(i));
		}

		init();

		expandNodes(expandedNodeIds);
	}

	private void expandNodes(ArrayList<Long> nodeIds) {
		while (nodeIds.size() != 0) {
			Long nodeId = nodeIds.get(0);
			for (int nodesPosition = 0; nodesPosition < getCount(); nodesPosition++) {
				if (getItemId(nodesPosition) == nodeId) {
					expand(nodesPosition);
					break;
				}
			}
			nodeIds.remove(0);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {				
		CodeMapBrowserItemView listItem = (CodeMapBrowserItemView) convertView;
		
		if (convertView == null)
			listItem = new CodeMapBrowserItemView(getContext());

		CodeMapBrowserItem item = getItem(position);
		listItem.setItem(item);

		File file = new File(projectDirectory.getAbsolutePath() + File.separator + item.name);
		listItem.setDirectory(file.isDirectory());
		listItem.setDeclarations(controller.getOpenDeclarations(getItemUrl(position)));
				
		return listItem;
	}


	@Override
	public void clear() {
		super.clear();
		this.expanded.clear();
	}

	@Override
	public void add(CodeMapBrowserItem item) {
		super.add(item);
		this.expanded.add(false);
	}

	@Override
	public void insert(CodeMapBrowserItem item, int index) {
		super.insert(item, index);
		this.expanded.add(index, false);
	}

	public void insertAll(ArrayList<CodeMapBrowserItem> nodes, int position) {
		Collections.reverse(nodes);
		for(CodeMapBrowserItem node: nodes)
			insert(node, position);
		notifyDataSetInvalidated();
	}

	@Override
	public void remove(CodeMapBrowserItem node) {
		int position = getPosition(node);
		this.expanded.remove(position);
		super.remove(node);
	}

	public boolean getExpanded(int position) {
		if(position < 0 || position > this.expanded.size())
			return false;

		return this.expanded.get(position);
	}

	public boolean collapseExpand(int position) {
		if(position >= getCount() || position >= this.expanded.size() || position < 0)
			return false;

		if(this.expanded.get(position))
			return collapse(getItem(position), position);
		else
			return expand(position);
	}

	public boolean collapse(CodeMapBrowserItem node, int position) {
		int activePos = position + 1;
		while(activePos < this.expanded.size()) {
			if(getItem(activePos).level <= node.level)
				break;
			collapse(getItem(activePos), activePos);
			remove(getItem(activePos));
		}
		this.expanded.set(position, false);
		return true;
	}

	public boolean expand(int position) {
		CodeMapBrowserItem item = getItem(position);
		ArrayList<CodeMapBrowserItem> children = item.getChildren(controller, getContext());
		Collections.sort(children, new CodeMapBrowserItemComparator());
		
		if(children == null || children.size() == 0)
			return false;
		
		insertAll(children, position + 1);
		this.expanded.set(position, true);
		return true;
	}

	
	@Override
	public long getItemId(int position) {
		return position;
	}

	public int findParent(int position) {
		if(position >= getCount() || position < 0)
			return -1;

		long currentLevel = getItem(position).level;
		for(int activePos = position - 1; activePos >= 0; activePos--) {
			if(getItem(activePos).level < currentLevel)
				return activePos;
		}

		return -1;
	}
	
	public String getItemUrl(int position) {
		CodeMapBrowserItem item = getItem(position);

		int parentId = findParent(position);

		if (parentId != -1) {
			CodeMapBrowserItem parentItem = getItem(parentId);
			String url = parentItem.name + ":" + item.name;
			return url;
		}

		return "";
	}
}
