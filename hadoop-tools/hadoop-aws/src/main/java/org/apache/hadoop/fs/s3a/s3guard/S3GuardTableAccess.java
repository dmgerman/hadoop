begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|ItemCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|QueryOutcome
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|ScanOutcome
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|internal
operator|.
name|IteratorSupport
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|spec
operator|.
name|QuerySpec
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|xspec
operator|.
name|ExpressionSpecBuilder
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|s3a
operator|.
name|S3AFileStatus
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
operator|.
name|DynamoDBMetadataStore
operator|.
name|VERSION_MARKER_ITEM_NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
operator|.
name|PathMetadataDynamoDBTranslation
operator|.
name|CHILD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
operator|.
name|PathMetadataDynamoDBTranslation
operator|.
name|PARENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
operator|.
name|PathMetadataDynamoDBTranslation
operator|.
name|TABLE_VERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
operator|.
name|PathMetadataDynamoDBTranslation
operator|.
name|itemToPathMetadata
import|;
end_import

begin_comment
comment|/**  * Package-scoped accessor to table state in S3Guard.  * This is for maintenance, diagnostics and testing: it is<i>not</i> to  * be used otherwise.  *<ol>  *<li>  *     Some of the operations here may dramatically alter the state of  *     a table, so use carefully.  *</li>  *<li>  *     Operations to assess consistency of a store are best executed  *     against a table which is otherwise inactive.  *</li>  *<li>  *     No retry/throttling or AWS to IOE logic here.  *</li>  *<li>  *     If a scan or query includes the version marker in the result, it  *     is converted to a {@link VersionMarker} instance.  *</li>  *</ol>  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|S3GuardTableAccess
class|class
name|S3GuardTableAccess
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
name|S3GuardTableAccess
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Store instance to work with.    */
DECL|field|store
specifier|private
specifier|final
name|DynamoDBMetadataStore
name|store
decl_stmt|;
comment|/**    * Table; retrieved from the store.    */
DECL|field|table
specifier|private
specifier|final
name|Table
name|table
decl_stmt|;
comment|/**    * Construct.    * @param store store to work with.    */
DECL|method|S3GuardTableAccess (final DynamoDBMetadataStore store)
name|S3GuardTableAccess
parameter_list|(
specifier|final
name|DynamoDBMetadataStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|checkNotNull
argument_list|(
name|store
operator|.
name|getTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Username of user in store.    * @return a string.    */
DECL|method|getUsername ()
specifier|private
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|store
operator|.
name|getUsername
argument_list|()
return|;
block|}
comment|/**    * Execute a query.    * @param spec query spec.    * @return the outcome.    */
DECL|method|query (QuerySpec spec)
name|ItemCollection
argument_list|<
name|QueryOutcome
argument_list|>
name|query
parameter_list|(
name|QuerySpec
name|spec
parameter_list|)
block|{
return|return
name|table
operator|.
name|query
argument_list|(
name|spec
argument_list|)
return|;
block|}
comment|/**    * Issue a query where the result is to be an iterator over    * the entries    * of DDBPathMetadata instances.    * @param spec query spec.    * @return an iterator over path entries.    */
DECL|method|queryMetadata (QuerySpec spec)
name|Iterable
argument_list|<
name|DDBPathMetadata
argument_list|>
name|queryMetadata
parameter_list|(
name|QuerySpec
name|spec
parameter_list|)
block|{
return|return
operator|new
name|DDBPathMetadataCollection
argument_list|<>
argument_list|(
name|query
argument_list|(
name|spec
argument_list|)
argument_list|)
return|;
block|}
DECL|method|scan (ExpressionSpecBuilder spec)
name|ItemCollection
argument_list|<
name|ScanOutcome
argument_list|>
name|scan
parameter_list|(
name|ExpressionSpecBuilder
name|spec
parameter_list|)
block|{
return|return
name|table
operator|.
name|scan
argument_list|(
name|spec
operator|.
name|buildForScan
argument_list|()
argument_list|)
return|;
block|}
DECL|method|scanMetadata (ExpressionSpecBuilder spec)
name|Iterable
argument_list|<
name|DDBPathMetadata
argument_list|>
name|scanMetadata
parameter_list|(
name|ExpressionSpecBuilder
name|spec
parameter_list|)
block|{
return|return
operator|new
name|DDBPathMetadataCollection
argument_list|<>
argument_list|(
name|scan
argument_list|(
name|spec
argument_list|)
argument_list|)
return|;
block|}
DECL|method|delete (Collection<Path> paths)
name|void
name|delete
parameter_list|(
name|Collection
argument_list|<
name|Path
argument_list|>
name|paths
parameter_list|)
block|{
name|paths
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|PathMetadataDynamoDBTranslation
operator|::
name|pathToKey
argument_list|)
operator|.
name|forEach
argument_list|(
name|table
operator|::
name|deleteItem
argument_list|)
expr_stmt|;
block|}
comment|/**    * A collection which wraps the result of a query or scan.    * Important: iterate through this only once; the outcome    * of repeating an iteration is "undefined"    * @param<T> type of outcome.    */
DECL|class|DDBPathMetadataCollection
specifier|private
specifier|final
class|class
name|DDBPathMetadataCollection
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|DDBPathMetadata
argument_list|>
block|{
comment|/**      * Query/scan result.      */
DECL|field|outcome
specifier|private
specifier|final
name|ItemCollection
argument_list|<
name|T
argument_list|>
name|outcome
decl_stmt|;
comment|/**      * Instantiate.      * @param outcome query/scan outcome.      */
DECL|method|DDBPathMetadataCollection (final ItemCollection<T> outcome)
specifier|private
name|DDBPathMetadataCollection
parameter_list|(
specifier|final
name|ItemCollection
argument_list|<
name|T
argument_list|>
name|outcome
parameter_list|)
block|{
name|this
operator|.
name|outcome
operator|=
name|outcome
expr_stmt|;
block|}
comment|/**      * Get the iterator.      * @return the iterator.      */
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|DDBPathMetadata
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DDBPathMetadataIterator
argument_list|<>
argument_list|(
name|outcome
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * An iterator which converts the iterated-over result of    * a query or scan into a {@code DDBPathMetadataIterator} entry.    * @param<T> type of source.    */
DECL|class|DDBPathMetadataIterator
specifier|private
specifier|final
class|class
name|DDBPathMetadataIterator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|DDBPathMetadata
argument_list|>
block|{
comment|/**      * Iterator to invoke.      */
DECL|field|it
specifier|private
specifier|final
name|IteratorSupport
argument_list|<
name|Item
argument_list|,
name|T
argument_list|>
name|it
decl_stmt|;
comment|/**      * Instantiate.      * @param it Iterator to invoke.      */
DECL|method|DDBPathMetadataIterator (final IteratorSupport<Item, T> it)
specifier|private
name|DDBPathMetadataIterator
parameter_list|(
specifier|final
name|IteratorSupport
argument_list|<
name|Item
argument_list|,
name|T
argument_list|>
name|it
parameter_list|)
block|{
name|this
operator|.
name|it
operator|=
name|it
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|DDBPathMetadata
name|next
parameter_list|()
block|{
name|Item
name|item
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|key
init|=
name|primaryKey
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERSION_MARKER_ITEM_NAME
operator|.
name|equals
argument_list|(
name|key
operator|.
name|getLeft
argument_list|()
argument_list|)
operator|&&
name|VERSION_MARKER_ITEM_NAME
operator|.
name|equals
argument_list|(
name|key
operator|.
name|getRight
argument_list|()
argument_list|)
condition|)
block|{
comment|// a version marker is found, return the special type
return|return
operator|new
name|VersionMarker
argument_list|(
name|item
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|itemToPathMetadata
argument_list|(
name|item
argument_list|,
name|getUsername
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * DDBPathMetadata subclass returned when a query returns    * the version marker.    * There is a FileStatus returned where the owner field contains    * the table version; the path is always the unqualified path "/VERSION".    * Because it is unqualified, operations which treat this as a normal    * DDB metadata entry usually fail.    */
DECL|class|VersionMarker
specifier|static
specifier|final
class|class
name|VersionMarker
extends|extends
name|DDBPathMetadata
block|{
comment|/**      * Instantiate.      * @param versionMarker the version marker.      */
DECL|method|VersionMarker (Item versionMarker)
name|VersionMarker
parameter_list|(
name|Item
name|versionMarker
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|S3AFileStatus
argument_list|(
literal|true
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/VERSION"
argument_list|)
argument_list|,
literal|""
operator|+
name|versionMarker
operator|.
name|getString
argument_list|(
name|TABLE_VERSION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Given an item, split it to the parent and child fields.    * @param item item to split.    * @return (parent, child).    */
DECL|method|primaryKey (Item item)
specifier|private
specifier|static
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|primaryKey
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|item
operator|.
name|getString
argument_list|(
name|PARENT
argument_list|)
argument_list|,
name|item
operator|.
name|getString
argument_list|(
name|CHILD
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

