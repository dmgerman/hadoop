begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
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
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
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
name|conf
operator|.
name|Configuration
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
name|Invoker
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
name|S3AInputPolicy
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
name|S3AInstrumentation
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
name|S3AStorageStatistics
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
name|Statistic
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
name|s3guard
operator|.
name|ITtlTimeProvider
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
name|s3guard
operator|.
name|MetadataStore
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
name|security
operator|.
name|UserGroupInformation
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
name|util
operator|.
name|SemaphoredDelegatingExecutor
import|;
end_import

begin_comment
comment|/**  * This class provides the core context of the S3A filesystem to subsidiary  * components, without exposing the entire parent class.  * This is eliminate explicit recursive coupling.  *  * Where methods on the FS are to be invoked, they are referenced  * via the {@link ContextAccessors} interface, so tests can implement  * their own.  *  *<i>Warning:</i> this really is private and unstable. Do not use  * outside the org.apache.hadoop.fs.s3a package.  */
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
DECL|class|StoreContext
specifier|public
class|class
name|StoreContext
block|{
comment|/** Filesystem URI. */
DECL|field|fsURI
specifier|private
specifier|final
name|URI
name|fsURI
decl_stmt|;
comment|/** Bucket name. */
DECL|field|bucket
specifier|private
specifier|final
name|String
name|bucket
decl_stmt|;
comment|/** FS configuration after all per-bucket overrides applied. */
DECL|field|configuration
specifier|private
specifier|final
name|Configuration
name|configuration
decl_stmt|;
comment|/** Username. */
DECL|field|username
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
comment|/** Principal who created the FS. */
DECL|field|owner
specifier|private
specifier|final
name|UserGroupInformation
name|owner
decl_stmt|;
comment|/**    * Bounded thread pool for async operations.    */
DECL|field|executor
specifier|private
specifier|final
name|ListeningExecutorService
name|executor
decl_stmt|;
comment|/**    * Capacity of new executors created.    */
DECL|field|executorCapacity
specifier|private
specifier|final
name|int
name|executorCapacity
decl_stmt|;
comment|/** Invoker of operations. */
DECL|field|invoker
specifier|private
specifier|final
name|Invoker
name|invoker
decl_stmt|;
comment|/** Instrumentation and statistics. */
DECL|field|instrumentation
specifier|private
specifier|final
name|S3AInstrumentation
name|instrumentation
decl_stmt|;
DECL|field|storageStatistics
specifier|private
specifier|final
name|S3AStorageStatistics
name|storageStatistics
decl_stmt|;
comment|/** Seek policy. */
DECL|field|inputPolicy
specifier|private
specifier|final
name|S3AInputPolicy
name|inputPolicy
decl_stmt|;
comment|/** How to react to changes in etags and versions. */
DECL|field|changeDetectionPolicy
specifier|private
specifier|final
name|ChangeDetectionPolicy
name|changeDetectionPolicy
decl_stmt|;
comment|/** Evaluated options. */
DECL|field|multiObjectDeleteEnabled
specifier|private
specifier|final
name|boolean
name|multiObjectDeleteEnabled
decl_stmt|;
comment|/** List algorithm. */
DECL|field|useListV1
specifier|private
specifier|final
name|boolean
name|useListV1
decl_stmt|;
comment|/**    * To allow this context to be passed down to the metastore, this field    * wll be null until initialized.    */
DECL|field|metadataStore
specifier|private
specifier|final
name|MetadataStore
name|metadataStore
decl_stmt|;
DECL|field|contextAccessors
specifier|private
specifier|final
name|ContextAccessors
name|contextAccessors
decl_stmt|;
comment|/**    * Source of time.    */
DECL|field|timeProvider
specifier|private
name|ITtlTimeProvider
name|timeProvider
decl_stmt|;
comment|/**    * Instantiate.    * No attempt to use a builder here as outside tests    * this should only be created in the S3AFileSystem.    */
DECL|method|StoreContext ( final URI fsURI, final String bucket, final Configuration configuration, final String username, final UserGroupInformation owner, final ListeningExecutorService executor, final int executorCapacity, final Invoker invoker, final S3AInstrumentation instrumentation, final S3AStorageStatistics storageStatistics, final S3AInputPolicy inputPolicy, final ChangeDetectionPolicy changeDetectionPolicy, final boolean multiObjectDeleteEnabled, final MetadataStore metadataStore, final boolean useListV1, final ContextAccessors contextAccessors, final ITtlTimeProvider timeProvider)
specifier|public
name|StoreContext
parameter_list|(
specifier|final
name|URI
name|fsURI
parameter_list|,
specifier|final
name|String
name|bucket
parameter_list|,
specifier|final
name|Configuration
name|configuration
parameter_list|,
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|UserGroupInformation
name|owner
parameter_list|,
specifier|final
name|ListeningExecutorService
name|executor
parameter_list|,
specifier|final
name|int
name|executorCapacity
parameter_list|,
specifier|final
name|Invoker
name|invoker
parameter_list|,
specifier|final
name|S3AInstrumentation
name|instrumentation
parameter_list|,
specifier|final
name|S3AStorageStatistics
name|storageStatistics
parameter_list|,
specifier|final
name|S3AInputPolicy
name|inputPolicy
parameter_list|,
specifier|final
name|ChangeDetectionPolicy
name|changeDetectionPolicy
parameter_list|,
specifier|final
name|boolean
name|multiObjectDeleteEnabled
parameter_list|,
specifier|final
name|MetadataStore
name|metadataStore
parameter_list|,
specifier|final
name|boolean
name|useListV1
parameter_list|,
specifier|final
name|ContextAccessors
name|contextAccessors
parameter_list|,
specifier|final
name|ITtlTimeProvider
name|timeProvider
parameter_list|)
block|{
name|this
operator|.
name|fsURI
operator|=
name|fsURI
expr_stmt|;
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|executorCapacity
operator|=
name|executorCapacity
expr_stmt|;
name|this
operator|.
name|invoker
operator|=
name|invoker
expr_stmt|;
name|this
operator|.
name|instrumentation
operator|=
name|instrumentation
expr_stmt|;
name|this
operator|.
name|storageStatistics
operator|=
name|storageStatistics
expr_stmt|;
name|this
operator|.
name|inputPolicy
operator|=
name|inputPolicy
expr_stmt|;
name|this
operator|.
name|changeDetectionPolicy
operator|=
name|changeDetectionPolicy
expr_stmt|;
name|this
operator|.
name|multiObjectDeleteEnabled
operator|=
name|multiObjectDeleteEnabled
expr_stmt|;
name|this
operator|.
name|metadataStore
operator|=
name|metadataStore
expr_stmt|;
name|this
operator|.
name|useListV1
operator|=
name|useListV1
expr_stmt|;
name|this
operator|.
name|contextAccessors
operator|=
name|contextAccessors
expr_stmt|;
name|this
operator|.
name|timeProvider
operator|=
name|timeProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone ()
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getFsURI ()
specifier|public
name|URI
name|getFsURI
parameter_list|()
block|{
return|return
name|fsURI
return|;
block|}
DECL|method|getBucket ()
specifier|public
name|String
name|getBucket
parameter_list|()
block|{
return|return
name|bucket
return|;
block|}
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
DECL|method|getUsername ()
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
DECL|method|getExecutor ()
specifier|public
name|ListeningExecutorService
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
DECL|method|getInvoker ()
specifier|public
name|Invoker
name|getInvoker
parameter_list|()
block|{
return|return
name|invoker
return|;
block|}
DECL|method|getInstrumentation ()
specifier|public
name|S3AInstrumentation
name|getInstrumentation
parameter_list|()
block|{
return|return
name|instrumentation
return|;
block|}
DECL|method|getInputPolicy ()
specifier|public
name|S3AInputPolicy
name|getInputPolicy
parameter_list|()
block|{
return|return
name|inputPolicy
return|;
block|}
DECL|method|getChangeDetectionPolicy ()
specifier|public
name|ChangeDetectionPolicy
name|getChangeDetectionPolicy
parameter_list|()
block|{
return|return
name|changeDetectionPolicy
return|;
block|}
DECL|method|isMultiObjectDeleteEnabled ()
specifier|public
name|boolean
name|isMultiObjectDeleteEnabled
parameter_list|()
block|{
return|return
name|multiObjectDeleteEnabled
return|;
block|}
DECL|method|getMetadataStore ()
specifier|public
name|MetadataStore
name|getMetadataStore
parameter_list|()
block|{
return|return
name|metadataStore
return|;
block|}
DECL|method|isUseListV1 ()
specifier|public
name|boolean
name|isUseListV1
parameter_list|()
block|{
return|return
name|useListV1
return|;
block|}
comment|/**    * Convert a key to a fully qualified path.    * @param key input key    * @return the fully qualified path including URI scheme and bucket name.    */
DECL|method|keyToPath (String key)
specifier|public
name|Path
name|keyToPath
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|contextAccessors
operator|.
name|keyToPath
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Turns a path (relative or otherwise) into an S3 key.    *    * @param path input path, may be relative to the working dir    * @return a key excluding the leading "/", or, if it is the root path, ""    */
DECL|method|pathToKey (Path path)
specifier|public
name|String
name|pathToKey
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|contextAccessors
operator|.
name|pathToKey
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Get the storage statistics of this filesystem.    * @return the storage statistics    */
DECL|method|getStorageStatistics ()
specifier|public
name|S3AStorageStatistics
name|getStorageStatistics
parameter_list|()
block|{
return|return
name|storageStatistics
return|;
block|}
comment|/**    * Increment a statistic by 1.    * This increments both the instrumentation and storage statistics.    * @param statistic The operation to increment    */
DECL|method|incrementStatistic (Statistic statistic)
specifier|public
name|void
name|incrementStatistic
parameter_list|(
name|Statistic
name|statistic
parameter_list|)
block|{
name|incrementStatistic
argument_list|(
name|statistic
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment a statistic by a specific value.    * This increments both the instrumentation and storage statistics.    * @param statistic The operation to increment    * @param count the count to increment    */
DECL|method|incrementStatistic (Statistic statistic, long count)
specifier|public
name|void
name|incrementStatistic
parameter_list|(
name|Statistic
name|statistic
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|instrumentation
operator|.
name|incrementCounter
argument_list|(
name|statistic
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|storageStatistics
operator|.
name|incrementCounter
argument_list|(
name|statistic
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decrement a gauge by a specific value.    * @param statistic The operation to decrement    * @param count the count to decrement    */
DECL|method|decrementGauge (Statistic statistic, long count)
specifier|public
name|void
name|decrementGauge
parameter_list|(
name|Statistic
name|statistic
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|instrumentation
operator|.
name|decrementGauge
argument_list|(
name|statistic
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment a gauge by a specific value.    * @param statistic The operation to increment    * @param count the count to increment    */
DECL|method|incrementGauge (Statistic statistic, long count)
specifier|public
name|void
name|incrementGauge
parameter_list|(
name|Statistic
name|statistic
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|instrumentation
operator|.
name|incrementGauge
argument_list|(
name|statistic
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new executor service with a given capacity.    * This executor submits works to the {@link #executor}, using a    * {@link SemaphoredDelegatingExecutor} to limit the number    * of requests coming in from a specific client.    *    * Because this delegates to an existing thread pool, the cost of    * creating a new instance here is low.    * As the throttling is per instance, separate instances    * should be created for each operation which wishes to execute work in    * parallel<i>without</i> saturating the base executor.    * This is important if either the duration of each operation is long    * or the submission rate of work is high.    * @param capacity maximum capacity of this executor.    * @return an executor for submitting work.    */
DECL|method|createThrottledExecutor (int capacity)
specifier|public
name|ListeningExecutorService
name|createThrottledExecutor
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
return|return
operator|new
name|SemaphoredDelegatingExecutor
argument_list|(
name|executor
argument_list|,
name|capacity
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Create a new executor with the capacity defined in    * {@link #executorCapacity}.    * @return a new executor for exclusive use by the caller.    */
DECL|method|createThrottledExecutor ()
specifier|public
name|ListeningExecutorService
name|createThrottledExecutor
parameter_list|()
block|{
return|return
name|createThrottledExecutor
argument_list|(
name|executorCapacity
argument_list|)
return|;
block|}
comment|/**    * Get the owner of the filesystem.    * @return the user who created this filesystem.    */
DECL|method|getOwner ()
specifier|public
name|UserGroupInformation
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Create a temporary file somewhere.    * @param prefix prefix for the temporary file    * @param size expected size.    * @return a file reference.    * @throws IOException failure.    */
DECL|method|createTempFile (String prefix, long size)
specifier|public
name|File
name|createTempFile
parameter_list|(
name|String
name|prefix
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|contextAccessors
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/**    * Get the location of the bucket.    * @return the bucket location.    * @throws IOException failure.    */
DECL|method|getBucketLocation ()
specifier|public
name|String
name|getBucketLocation
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|contextAccessors
operator|.
name|getBucketLocation
argument_list|()
return|;
block|}
comment|/**    * Get the time provider.    * @return the time source.    */
DECL|method|getTimeProvider ()
specifier|public
name|ITtlTimeProvider
name|getTimeProvider
parameter_list|()
block|{
return|return
name|timeProvider
return|;
block|}
comment|/**    * Build the full S3 key for a request from the status entry,    * possibly adding a "/" if it represents directory and it does    * not have a trailing slash already.    * @param stat status to build the key from    * @return a key for a delete request    */
DECL|method|fullKey (final S3AFileStatus stat)
specifier|public
name|String
name|fullKey
parameter_list|(
specifier|final
name|S3AFileStatus
name|stat
parameter_list|)
block|{
name|String
name|k
init|=
name|pathToKey
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|stat
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|k
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|)
condition|?
name|k
operator|+
literal|"/"
else|:
name|k
return|;
block|}
block|}
end_class

end_unit

