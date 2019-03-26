begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Unstable
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
name|FileSystem
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
name|UnsupportedFileSystemException
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
name|authorize
operator|.
name|AccessControlList
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
name|service
operator|.
name|AbstractService
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|QueueACL
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
name|yarn
operator|.
name|security
operator|.
name|AccessType
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
name|yarn
operator|.
name|security
operator|.
name|Permission
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
name|yarn
operator|.
name|security
operator|.
name|PrivilegedEntity
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
name|yarn
operator|.
name|security
operator|.
name|PrivilegedEntity
operator|.
name|EntityType
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerUtils
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocation
operator|.
name|AllocationFileParser
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocation
operator|.
name|AllocationFileQueueParser
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocation
operator|.
name|QueueProperties
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
name|yarn
operator|.
name|util
operator|.
name|Clock
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
name|yarn
operator|.
name|util
operator|.
name|SystemClock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|URL
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocation
operator|.
name|AllocationFileQueueParser
operator|.
name|EVERYBODY_ACL
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocation
operator|.
name|AllocationFileQueueParser
operator|.
name|ROOT
import|;
end_import

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|AllocationFileLoaderService
specifier|public
class|class
name|AllocationFileLoaderService
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AllocationFileLoaderService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Time to wait between checks of the allocation file */
DECL|field|ALLOC_RELOAD_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|long
name|ALLOC_RELOAD_INTERVAL_MS
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|/**    * Time to wait after the allocation has been modified before reloading it    * (this is done to prevent loading a file that hasn't been fully written).    */
DECL|field|ALLOC_RELOAD_WAIT_MS
specifier|public
specifier|static
specifier|final
name|long
name|ALLOC_RELOAD_WAIT_MS
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
DECL|field|THREAD_JOIN_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|long
name|THREAD_JOIN_TIMEOUT_MS
init|=
literal|1000
decl_stmt|;
comment|//Permitted allocation file filesystems (case insensitive)
DECL|field|SUPPORTED_FS_REGEX
specifier|private
specifier|static
specifier|final
name|String
name|SUPPORTED_FS_REGEX
init|=
literal|"(?i)(hdfs)|(file)|(s3a)|(viewfs)"
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|FairScheduler
name|scheduler
decl_stmt|;
comment|// Last time we successfully reloaded queues
DECL|field|lastSuccessfulReload
specifier|private
specifier|volatile
name|long
name|lastSuccessfulReload
decl_stmt|;
DECL|field|lastReloadAttemptFailed
specifier|private
specifier|volatile
name|boolean
name|lastReloadAttemptFailed
init|=
literal|false
decl_stmt|;
comment|// Path to XML file containing allocations.
DECL|field|allocFile
specifier|private
name|Path
name|allocFile
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|reloadListener
specifier|private
name|Listener
name|reloadListener
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|reloadIntervalMs
name|long
name|reloadIntervalMs
init|=
name|ALLOC_RELOAD_INTERVAL_MS
decl_stmt|;
DECL|field|reloadThread
specifier|private
name|Thread
name|reloadThread
decl_stmt|;
DECL|field|running
specifier|private
specifier|volatile
name|boolean
name|running
init|=
literal|true
decl_stmt|;
DECL|method|AllocationFileLoaderService (FairScheduler scheduler)
name|AllocationFileLoaderService
parameter_list|(
name|FairScheduler
name|scheduler
parameter_list|)
block|{
name|this
argument_list|(
name|SystemClock
operator|.
name|getInstance
argument_list|()
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
block|}
DECL|field|defaultPermissions
specifier|private
name|List
argument_list|<
name|Permission
argument_list|>
name|defaultPermissions
decl_stmt|;
DECL|method|AllocationFileLoaderService (Clock clock, FairScheduler scheduler)
name|AllocationFileLoaderService
parameter_list|(
name|Clock
name|clock
parameter_list|,
name|FairScheduler
name|scheduler
parameter_list|)
block|{
name|super
argument_list|(
name|AllocationFileLoaderService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|allocFile
operator|=
name|getAllocationFile
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|allocFile
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fs
operator|=
name|allocFile
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|reloadThread
operator|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|running
condition|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|reloadListener
operator|.
name|onCheck
argument_list|()
expr_stmt|;
block|}
name|long
name|time
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|lastModified
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|allocFile
argument_list|)
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastModified
operator|>
name|lastSuccessfulReload
operator|&&
name|time
operator|>
name|lastModified
operator|+
name|ALLOC_RELOAD_WAIT_MS
condition|)
block|{
try|try
block|{
name|reloadAllocations
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|lastReloadAttemptFailed
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to reload fair scheduler config file - "
operator|+
literal|"will use existing allocations."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|lastReloadAttemptFailed
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|lastModified
operator|==
literal|0l
condition|)
block|{
if|if
condition|(
operator|!
name|lastReloadAttemptFailed
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to reload fair scheduler config file because"
operator|+
literal|" last modified returned 0. File exists: "
operator|+
name|fs
operator|.
name|exists
argument_list|(
name|allocFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastReloadAttemptFailed
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while loading allocation file: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|reloadIntervalMs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted while waiting to reload alloc configuration"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|reloadThread
operator|.
name|setName
argument_list|(
literal|"AllocationFileReloader"
argument_list|)
expr_stmt|;
name|reloadThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|reloadThread
operator|!=
literal|null
condition|)
block|{
name|reloadThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|running
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|reloadThread
operator|!=
literal|null
condition|)
block|{
name|reloadThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|reloadThread
operator|.
name|join
argument_list|(
name|THREAD_JOIN_TIMEOUT_MS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"reloadThread fails to join."
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Path to XML file containing allocations. If the    * path is relative, it is searched for in the    * classpath, but loaded like a regular File.    */
annotation|@
name|VisibleForTesting
DECL|method|getAllocationFile (Configuration conf)
name|Path
name|getAllocationFile
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|UnsupportedFileSystemException
block|{
name|String
name|allocFilePath
init|=
name|conf
operator|.
name|get
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ALLOCATION_FILE
argument_list|,
name|FairSchedulerConfiguration
operator|.
name|DEFAULT_ALLOCATION_FILE
argument_list|)
decl_stmt|;
name|Path
name|allocPath
init|=
operator|new
name|Path
argument_list|(
name|allocFilePath
argument_list|)
decl_stmt|;
name|String
name|allocPathScheme
init|=
name|allocPath
operator|.
name|toUri
argument_list|()
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
name|allocPathScheme
operator|!=
literal|null
operator|&&
operator|!
name|allocPathScheme
operator|.
name|matches
argument_list|(
name|SUPPORTED_FS_REGEX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedFileSystemException
argument_list|(
literal|"Allocation file "
operator|+
name|allocFilePath
operator|+
literal|" uses an unsupported filesystem"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|allocPath
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|allocFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|allocFilePath
operator|+
literal|" not found on the classpath."
argument_list|)
expr_stmt|;
name|allocPath
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|url
operator|.
name|getProtocol
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Allocation file "
operator|+
name|url
operator|+
literal|" found on the classpath is not on the local filesystem."
argument_list|)
throw|;
block|}
else|else
block|{
name|allocPath
operator|=
operator|new
name|Path
argument_list|(
name|url
operator|.
name|getProtocol
argument_list|()
argument_list|,
literal|null
argument_list|,
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|allocPath
operator|.
name|isAbsoluteAndSchemeAuthorityNull
argument_list|()
condition|)
block|{
name|allocPath
operator|=
operator|new
name|Path
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|,
name|allocFilePath
argument_list|)
expr_stmt|;
block|}
return|return
name|allocPath
return|;
block|}
DECL|method|setReloadListener (Listener reloadListener)
specifier|public
specifier|synchronized
name|void
name|setReloadListener
parameter_list|(
name|Listener
name|reloadListener
parameter_list|)
block|{
name|this
operator|.
name|reloadListener
operator|=
name|reloadListener
expr_stmt|;
block|}
comment|/**    * Updates the allocation list from the allocation config file. This file is    * expected to be in the XML format specified in the design doc.    *    * @throws IOException if the config file cannot be read.    * @throws AllocationConfigurationException if allocations are invalid.    * @throws ParserConfigurationException if XML parser is misconfigured.    * @throws SAXException if config file is malformed.    */
DECL|method|reloadAllocations ()
specifier|public
specifier|synchronized
name|void
name|reloadAllocations
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|AllocationConfigurationException
block|{
if|if
condition|(
name|allocFile
operator|==
literal|null
condition|)
block|{
name|reloadListener
operator|.
name|onReload
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading allocation file "
operator|+
name|allocFile
argument_list|)
expr_stmt|;
comment|// Read and parse the allocations file.
name|DocumentBuilderFactory
name|docBuilderFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|docBuilderFactory
operator|.
name|setIgnoringComments
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|docBuilderFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|allocFile
argument_list|)
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"allocations"
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
literal|"Bad fair scheduler config "
operator|+
literal|"file: top-level element not<allocations>"
argument_list|)
throw|;
block|}
name|NodeList
name|elements
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|AllocationFileParser
name|allocationFileParser
init|=
operator|new
name|AllocationFileParser
argument_list|(
name|elements
argument_list|)
decl_stmt|;
name|allocationFileParser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|AllocationFileQueueParser
name|queueParser
init|=
operator|new
name|AllocationFileQueueParser
argument_list|(
name|allocationFileParser
operator|.
name|getQueueElements
argument_list|()
argument_list|)
decl_stmt|;
name|QueueProperties
name|queueProperties
init|=
name|queueParser
operator|.
name|parse
argument_list|()
decl_stmt|;
comment|// Load placement policy
name|getQueuePlacementPolicy
argument_list|(
name|allocationFileParser
argument_list|)
expr_stmt|;
name|setupRootQueueProperties
argument_list|(
name|allocationFileParser
argument_list|,
name|queueProperties
argument_list|)
expr_stmt|;
name|ReservationQueueConfiguration
name|globalReservationQueueConfig
init|=
name|createReservationQueueConfig
argument_list|(
name|allocationFileParser
argument_list|)
decl_stmt|;
name|AllocationConfiguration
name|info
init|=
operator|new
name|AllocationConfiguration
argument_list|(
name|queueProperties
argument_list|,
name|allocationFileParser
argument_list|,
name|globalReservationQueueConfig
argument_list|)
decl_stmt|;
name|lastSuccessfulReload
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|lastReloadAttemptFailed
operator|=
literal|false
expr_stmt|;
name|reloadListener
operator|.
name|onReload
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|getQueuePlacementPolicy ( AllocationFileParser allocationFileParser)
specifier|private
name|void
name|getQueuePlacementPolicy
parameter_list|(
name|AllocationFileParser
name|allocationFileParser
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
if|if
condition|(
name|allocationFileParser
operator|.
name|getQueuePlacementPolicy
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|QueuePlacementPolicy
operator|.
name|fromXml
argument_list|(
name|allocationFileParser
operator|.
name|getQueuePlacementPolicy
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QueuePlacementPolicy
operator|.
name|fromConfiguration
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setupRootQueueProperties ( AllocationFileParser allocationFileParser, QueueProperties queueProperties)
specifier|private
name|void
name|setupRootQueueProperties
parameter_list|(
name|AllocationFileParser
name|allocationFileParser
parameter_list|,
name|QueueProperties
name|queueProperties
parameter_list|)
block|{
comment|// Set the min/fair share preemption timeout for the root queue
if|if
condition|(
operator|!
name|queueProperties
operator|.
name|getMinSharePreemptionTimeouts
argument_list|()
operator|.
name|containsKey
argument_list|(
name|QueueManager
operator|.
name|ROOT_QUEUE
argument_list|)
condition|)
block|{
name|queueProperties
operator|.
name|getMinSharePreemptionTimeouts
argument_list|()
operator|.
name|put
argument_list|(
name|QueueManager
operator|.
name|ROOT_QUEUE
argument_list|,
name|allocationFileParser
operator|.
name|getDefaultMinSharePreemptionTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|queueProperties
operator|.
name|getFairSharePreemptionTimeouts
argument_list|()
operator|.
name|containsKey
argument_list|(
name|QueueManager
operator|.
name|ROOT_QUEUE
argument_list|)
condition|)
block|{
name|queueProperties
operator|.
name|getFairSharePreemptionTimeouts
argument_list|()
operator|.
name|put
argument_list|(
name|QueueManager
operator|.
name|ROOT_QUEUE
argument_list|,
name|allocationFileParser
operator|.
name|getDefaultFairSharePreemptionTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Set the fair share preemption threshold for the root queue
if|if
condition|(
operator|!
name|queueProperties
operator|.
name|getFairSharePreemptionThresholds
argument_list|()
operator|.
name|containsKey
argument_list|(
name|QueueManager
operator|.
name|ROOT_QUEUE
argument_list|)
condition|)
block|{
name|queueProperties
operator|.
name|getFairSharePreemptionThresholds
argument_list|()
operator|.
name|put
argument_list|(
name|QueueManager
operator|.
name|ROOT_QUEUE
argument_list|,
name|allocationFileParser
operator|.
name|getDefaultFairSharePreemptionThreshold
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createReservationQueueConfig ( AllocationFileParser allocationFileParser)
specifier|private
name|ReservationQueueConfiguration
name|createReservationQueueConfig
parameter_list|(
name|AllocationFileParser
name|allocationFileParser
parameter_list|)
block|{
name|ReservationQueueConfiguration
name|globalReservationQueueConfig
init|=
operator|new
name|ReservationQueueConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|allocationFileParser
operator|.
name|getReservationPlanner
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|globalReservationQueueConfig
operator|.
name|setPlanner
argument_list|(
name|allocationFileParser
operator|.
name|getReservationPlanner
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allocationFileParser
operator|.
name|getReservationAdmissionPolicy
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|globalReservationQueueConfig
operator|.
name|setReservationAdmissionPolicy
argument_list|(
name|allocationFileParser
operator|.
name|getReservationAdmissionPolicy
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allocationFileParser
operator|.
name|getReservationAgent
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|globalReservationQueueConfig
operator|.
name|setReservationAgent
argument_list|(
name|allocationFileParser
operator|.
name|getReservationAgent
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|globalReservationQueueConfig
return|;
block|}
comment|/**    * Returns the list of default permissions.    * The default permission for the root queue is everybody ("*")    * and the default permission for all other queues is nobody ("").    * The default permission list would be loaded before the permissions    * from allocation file.    * @return default permission list    */
DECL|method|getDefaultPermissions ()
specifier|protected
name|List
argument_list|<
name|Permission
argument_list|>
name|getDefaultPermissions
parameter_list|()
block|{
if|if
condition|(
name|defaultPermissions
operator|==
literal|null
condition|)
block|{
name|defaultPermissions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|AccessType
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueueACL
name|acl
range|:
name|QueueACL
operator|.
name|values
argument_list|()
control|)
block|{
name|acls
operator|.
name|put
argument_list|(
name|SchedulerUtils
operator|.
name|toAccessType
argument_list|(
name|acl
argument_list|)
argument_list|,
name|EVERYBODY_ACL
argument_list|)
expr_stmt|;
block|}
name|defaultPermissions
operator|.
name|add
argument_list|(
operator|new
name|Permission
argument_list|(
operator|new
name|PrivilegedEntity
argument_list|(
name|EntityType
operator|.
name|QUEUE
argument_list|,
name|ROOT
argument_list|)
argument_list|,
name|acls
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|defaultPermissions
return|;
block|}
DECL|interface|Listener
specifier|public
interface|interface
name|Listener
block|{
DECL|method|onReload (AllocationConfiguration info)
name|void
name|onReload
parameter_list|(
name|AllocationConfiguration
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onCheck ()
specifier|default
name|void
name|onCheck
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

