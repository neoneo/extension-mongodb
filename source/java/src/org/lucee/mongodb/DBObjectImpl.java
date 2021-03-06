/**
 *
 * Copyright (c) 2015, Lucee Association Switzerland. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package org.lucee.mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lucee.runtime.PageContext;
import lucee.runtime.exp.PageException;
import lucee.runtime.type.Collection;
import lucee.runtime.type.Struct;

import org.lucee.mongodb.support.DBObjectImplSupport;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DBObjectImpl extends DBObjectImplSupport {

	private DBObject obj;

	public DBObjectImpl(DBObject obj) {
		if(obj==null) throw new RuntimeException();
		this.obj=obj;
	}

	@Override
	public int size() {
		return obj.keySet().size();
	}

	@Override
	public Key[] keys() {
		Iterator<String> it = obj.keySet().iterator();
		List<Key> list=new ArrayList<Key>();
		while(it.hasNext()){
			list.add(caster.toKey(it.next(),null));
		}
		return list.toArray(new Key[list.size()]);
	}

	@Override
	public Iterator<Key> keyIterator() {
		return new KeyIterator(caster, obj.keySet().iterator());
	}

	@Override
	public Object remove(Key key) throws PageException {
		if(obj.containsField(key.getString()))
			return obj.removeField(key.getString());
		throw exp.createApplicationException("There is no key ["+key+"] in the DBObject");
		
	}

	@Override
	public Object removeEL(Key key) {
		return obj.removeField(key.getString());
	}

	// TODO was not existing in 4.5 @Override
	public Object remove(Key key, Object defaultValue) {
		Object rtn = obj.removeField(key.getString());
		return rtn==null?defaultValue:rtn;
	}

	@Override
	public void clear() {
		Iterator<String> it = obj.keySet().iterator();
		while(it.hasNext()){
			obj.removeField(it.next());
		}
	}

	@Override
	public Object get(String key) throws PageException {
		if(obj.containsField(key)) return toCFML(obj.get(key));
		throw exp.createApplicationException("There is no key ["+key+"] in the DBObject");
	}

	@Override
	public Object get(String key, Object defaultValue) {
		if(obj.containsField(key)) return toCFML(obj.get(key));
		return defaultValue;
	}

	@Override
	public Object set(String key, Object value) throws PageException {
		obj.put(key, toMongo(value));
		return value;
	}

	@Override
	public Object setEL(String key, Object value) {
		obj.put(key, toMongo(value));
		return value;
	}

	@Override
	public final Collection duplicate(boolean deepCopy) {
		return new DBObjectImpl(new BasicDBObject(obj.toMap()));
	}
	
	@Override
	public boolean containsKey(String key) {
		return obj.containsField(key);
	}

	@Override
	public Iterator<String> keysAsStringIterator() {
		return obj.keySet().iterator();
	}

	/*
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this, obj
				.keySet()
				.iterator());
	}

	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(caster, this, obj.keySet().iterator());
	}*/

	@Override
	public Object call(PageContext pc, Key methodName, Object[] args) throws PageException {

		// containsField
		if(methodName.equals("containsField")) {
			checkArgLength("containsField",args,1,1);
			return toCFML(obj.containsField(
					caster.toString(args[0])
			));
		}
		// get
		if(methodName.equals("get")) {
			checkArgLength("get",args,1,1);
			return toCFML(obj.get(
					caster.toString(args[0])
			));
		}
		// isPartialObject
		if(methodName.equals("isPartialObject")) {
			checkArgLength("isPartialObject",args,0,0);
			return toCFML(obj.isPartialObject());
		}
		// markAsPartialObject
		if(methodName.equals("markAsPartialObject")) {
			checkArgLength("markAsPartialObject",args,0,0);
			obj.markAsPartialObject();
			return null;
		}
		// removeField
		if(methodName.equals("removeField")) {
			checkArgLength("removeField",args,1,1);
			return toCFML(obj.removeField(
					caster.toString(args[0])
			));
		}
		// toMap
		if(methodName.equals("toMap")) {
			checkArgLength("toMap",args,0,0);
			return toCFML(obj.toMap());
		}
		
		String functionNames="containsField,get,isPartialObject,markAsPartialObject,removeField,toMap";

		throw exp.createApplicationException("function "+methodName+" does not exist existing functions are ["+functionNames+"]");
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		throw new UnsupportedOperationException("named arguments are not supported yet!");
	}

	@Override
	public Set keySet() {
		return obj.keySet();
	}

	public DBObject getDBObject() {
		return obj;
	}

	// dummy that is no longer used in 5.0
	public long sizeOf() {
		return 0;
	}

}
