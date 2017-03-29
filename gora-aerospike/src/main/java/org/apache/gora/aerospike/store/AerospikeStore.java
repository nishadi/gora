/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.gora.aerospike.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.aerospike.client.*;
import org.apache.avro.Schema;
import org.apache.gora.aerospike.query.AerospikeQuery;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.PartitionQuery;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.store.impl.DataStoreBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Implementation of a Aerospike data store to be used by gora.
 *
 * @param <K>
 *            class to be used for the key
 * @param <T>
 *            class to be persisted within the store
 */
public class AerospikeStore<K,T extends PersistentBase> extends DataStoreBase<K,T> {

  public static final Logger LOG = LoggerFactory.getLogger(AerospikeStore.class);

  // Server IP property for Aerospike server
  private static final String AS_SERVER_IP = "server.ip";

  // Server IP property for Aerospike server
  private static final String AS_SERVER_port = "server.port";

  // Default server IP for Aerospike server
  private static final String DEFAULT_SERVER_IP = "localhost";

  // Default server port for Aerospike server
  private static final String DEFAULT_SERVER_PORT = "3000";

  private AerospikeClient aerospikeClient;

  @Override
  public void initialize(Class<K> keyClass, Class<T> persistentClass, Properties properties) {
      super.initialize(keyClass, persistentClass, properties);

      String serverIp = DataStoreFactory.findProperty(properties, this, AS_SERVER_IP, DEFAULT_SERVER_IP);
      int serverPort = Integer.parseInt(DataStoreFactory.findProperty(properties, this, AS_SERVER_port, DEFAULT_SERVER_PORT));
      aerospikeClient = new AerospikeClient(serverIp, serverPort);
  }

  @Override
  public String getSchemaName() {
    return null;
  }

  @Override
  public void createSchema() {
  }

  @Override
  public void deleteSchema() {
  }

  @Override
  public boolean schemaExists() {
    return true;
  }

  @Override
  public T get(K key, String[] fields) {
    return  null;
  }

  @Override
  public void put(K key, T val) {

    Schema schema = val.getSchema();
    List<Schema.Field> fields = schema.getFields();
    List<Bin> binsList = new ArrayList();
    for (int i = 0; i < fields.size(); i++) {
      if (!val.isDirty(i)) {
        continue;
      }
      Schema.Field field = fields.get(i);
      Object fieldValueObject = val.get(field.name());

      // ToDo: Provide support for generic Object type for the field value
      Bin bin1 = new Bin(field.name(), fieldValueObject.toString());
      binsList.add(bin1);
    }

    // ToDo: Obtain the database name and table name from the mapping file
    Key asKey = new Key("test", "AccessLog", key.toString());
    aerospikeClient.put(null, asKey, binsList.toArray(new Bin[binsList.size()]));
  }

  @Override
  public boolean delete(K key) {
    return true;
  }

  @Override
  public long deleteByQuery(Query<K,T> query) {
    return 0;
  }

  @Override
  public Result<K,T> execute(Query<K,T> query) {
    return null;
  }

  @Override
  public Query<K,T> newQuery() {
    return new AerospikeQuery<>(this);
  }

  @Override
  public List<PartitionQuery<K,T>> getPartitions(Query<K,T> query) throws IOException {
    return null;
  }

  public void flush() {
  }

  @Override
  public void close() {
    aerospikeClient.close();
  }
}
