/*
 * 
 * Copyright (c) 2016 1&1 Internet SE.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 *        
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.oneandone.relesia.database.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class EntityManagerUtil {
	private static EntityManagerFactory emf = null;
	private static EntityManager em = buildEntityManager().createEntityManager();

	private static EntityManagerFactory buildEntityManager() {
		EntityManagerFactory emf = null;
		try {
			if (emf == null) {
				emf = Persistence.createEntityManagerFactory("siadb");
			}
			return emf;
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
	
	public static EntityManager getEntityManager() {
		return em;
	}
}
