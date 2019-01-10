package com.gaoshuhang.imgserver.cache;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程安全的LinkedHashMap
 *
 * @author CiyaZ
 */
public class LruLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
	private final int maxCapacity;
	private final Lock lock = new ReentrantLock();

	public LruLinkedHashMap(int maxCapacity)
	{
		super(maxCapacity, 0.75f, true);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
	{
		return size() > maxCapacity;
	}

	@Override
	public V get(Object key)
	{
		lock.lock();
		V result = super.get(key);
		lock.unlock();
		return result;
	}

	@Override
	public V put(K key, V value)
	{
		lock.lock();
		V result = super.put(key, value);
		lock.unlock();
		return result;
	}
}
