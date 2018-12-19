begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|BaseRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Set of utility functions used to work with the State Store.  */
end_comment

begin_class
DECL|class|StateStoreUtils
specifier|public
specifier|final
class|class
name|StateStoreUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StateStoreUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|StateStoreUtils ()
specifier|private
name|StateStoreUtils
parameter_list|()
block|{
comment|// Utility class
block|}
comment|/**    * Get the base class for a record class. If we get an implementation of a    * record we will return the real parent record class.    *    * @param<T> Type of the class of the data record to check.    * @param clazz Class of the data record to check.    * @return Base class for the record.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
DECL|method|getRecordClass (final Class<T> clazz)
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
name|getRecordClass
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
comment|// We ignore the Impl classes and go to the super class
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
name|actualClazz
init|=
name|clazz
decl_stmt|;
while|while
condition|(
name|actualClazz
operator|.
name|getSimpleName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"Impl"
argument_list|)
condition|)
block|{
name|actualClazz
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
operator|)
name|actualClazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
comment|// Check if we went too far
if|if
condition|(
name|actualClazz
operator|.
name|equals
argument_list|(
name|BaseRecord
operator|.
name|class
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"We went too far ({}) with {}"
argument_list|,
name|actualClazz
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|actualClazz
operator|=
name|clazz
expr_stmt|;
block|}
return|return
name|actualClazz
return|;
block|}
comment|/**    * Get the base class for a record. If we get an implementation of a record we    * will return the real parent record class.    *    * @param<T> Type of the class of the data record.    * @param record Record to check its main class.    * @return Base class for the record.    */
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
DECL|method|getRecordClass (final T record)
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
name|getRecordClass
parameter_list|(
specifier|final
name|T
name|record
parameter_list|)
block|{
return|return
name|getRecordClass
argument_list|(
name|record
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the base class name for a record. If we get an implementation of a    * record we will return the real parent record class.    *    * @param<T> Type of the class of the data record.    * @param clazz Class of the data record to check.    * @return Name of the base class for the record.    */
DECL|method|getRecordName ( final Class<T> clazz)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|String
name|getRecordName
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|getRecordClass
argument_list|(
name|clazz
argument_list|)
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
comment|/**    * Filters a list of records to find all records matching the query.    *    * @param<T> Type of the class of the data record.    * @param query Map of field names and objects to use to filter results.    * @param records List of data records to filter.    * @return List of all records matching the query (or empty list if none    *         match), null if the data set could not be filtered.    */
DECL|method|filterMultiple ( final Query<T> query, final Iterable<T> records)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|filterMultiple
parameter_list|(
specifier|final
name|Query
argument_list|<
name|T
argument_list|>
name|query
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|records
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|matchingList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|T
name|record
range|:
name|records
control|)
block|{
if|if
condition|(
name|query
operator|.
name|matches
argument_list|(
name|record
argument_list|)
condition|)
block|{
name|matchingList
operator|.
name|add
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|matchingList
return|;
block|}
comment|/**    * Returns address in form of host:port, empty string if address is null.    *    * @param address address    * @return host:port    */
DECL|method|getHostPortString (InetSocketAddress address)
specifier|public
specifier|static
name|String
name|getHostPortString
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|address
condition|)
block|{
return|return
literal|""
return|;
block|}
name|String
name|hostName
init|=
name|address
operator|.
name|getHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|hostName
operator|.
name|equals
argument_list|(
literal|"0.0.0.0"
argument_list|)
condition|)
block|{
try|try
block|{
name|hostName
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get local host name"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
block|}
return|return
name|hostName
operator|+
literal|":"
operator|+
name|address
operator|.
name|getPort
argument_list|()
return|;
block|}
block|}
end_class

end_unit

