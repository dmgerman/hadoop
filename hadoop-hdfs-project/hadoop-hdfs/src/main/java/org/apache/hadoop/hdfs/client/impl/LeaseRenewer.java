begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Iterator
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|HadoopIllegalArgumentException
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
name|hdfs
operator|.
name|DFSClient
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
name|DFSOutputStream
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
name|protocol
operator|.
name|HdfsConstants
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
name|Daemon
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
name|StringUtils
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
name|Time
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  *<p>  * Used by {@link org.apache.hadoop.hdfs.DFSClient} for renewing file-being-written leases  * on the namenode.  * When a file is opened for write (create or append),  * namenode stores a file lease for recording the identity of the writer.  * The writer (i.e. the DFSClient) is required to renew the lease periodically.  * When the lease is not renewed before it expires,  * the namenode considers the writer as failed and then it may either let  * another writer to obtain the lease or close the file.  *</p>  *<p>  * This class also provides the following functionality:  *<ul>  *<li>  * It maintains a map from (namenode, user) pairs to lease renewers.  * The same {@link LeaseRenewer} instance is used for renewing lease  * for all the {@link org.apache.hadoop.hdfs.DFSClient} to the same namenode and the same user.  *</li>  *<li>  * Each renewer maintains a list of {@link org.apache.hadoop.hdfs.DFSClient}.  * Periodically the leases for all the clients are renewed.  * A client is removed from the list when the client is closed.  *</li>  *<li>  * A thread per namenode per user is used by the {@link LeaseRenewer}  * to renew the leases.  *</li>  *</ul>  *</p>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LeaseRenewer
specifier|public
class|class
name|LeaseRenewer
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LeaseRenewer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LEASE_RENEWER_GRACE_DEFAULT
specifier|static
specifier|final
name|long
name|LEASE_RENEWER_GRACE_DEFAULT
init|=
literal|60
operator|*
literal|1000L
decl_stmt|;
DECL|field|LEASE_RENEWER_SLEEP_DEFAULT
specifier|static
specifier|final
name|long
name|LEASE_RENEWER_SLEEP_DEFAULT
init|=
literal|1000L
decl_stmt|;
comment|/** Get a {@link LeaseRenewer} instance */
DECL|method|getInstance (final String authority, final UserGroupInformation ugi, final DFSClient dfsc)
specifier|public
specifier|static
name|LeaseRenewer
name|getInstance
parameter_list|(
specifier|final
name|String
name|authority
parameter_list|,
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|DFSClient
name|dfsc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeaseRenewer
name|r
init|=
name|Factory
operator|.
name|INSTANCE
operator|.
name|get
argument_list|(
name|authority
argument_list|,
name|ugi
argument_list|)
decl_stmt|;
name|r
operator|.
name|addClient
argument_list|(
name|dfsc
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/**    * A factory for sharing {@link LeaseRenewer} objects    * among {@link DFSClient} instances    * so that there is only one renewer per authority per user.    */
DECL|class|Factory
specifier|private
specifier|static
class|class
name|Factory
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|Factory
name|INSTANCE
init|=
operator|new
name|Factory
argument_list|()
decl_stmt|;
DECL|class|Key
specifier|private
specifier|static
class|class
name|Key
block|{
comment|/** Namenode info */
DECL|field|authority
specifier|final
name|String
name|authority
decl_stmt|;
comment|/** User info */
DECL|field|ugi
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|method|Key (final String authority, final UserGroupInformation ugi)
specifier|private
name|Key
parameter_list|(
specifier|final
name|String
name|authority
parameter_list|,
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"authority == null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"ugi == null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|authority
operator|=
name|authority
expr_stmt|;
name|this
operator|.
name|ugi
operator|=
name|ugi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|authority
operator|.
name|hashCode
argument_list|()
operator|^
name|ugi
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|Key
condition|)
block|{
specifier|final
name|Key
name|that
init|=
operator|(
name|Key
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|authority
operator|.
name|equals
argument_list|(
name|that
operator|.
name|authority
argument_list|)
operator|&&
name|this
operator|.
name|ugi
operator|.
name|equals
argument_list|(
name|that
operator|.
name|ugi
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|"@"
operator|+
name|authority
return|;
block|}
block|}
comment|/** A map for per user per namenode renewers. */
DECL|field|renewers
specifier|private
specifier|final
name|Map
argument_list|<
name|Key
argument_list|,
name|LeaseRenewer
argument_list|>
name|renewers
init|=
operator|new
name|HashMap
argument_list|<
name|Key
argument_list|,
name|LeaseRenewer
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Get a renewer. */
DECL|method|get (final String authority, final UserGroupInformation ugi)
specifier|private
specifier|synchronized
name|LeaseRenewer
name|get
parameter_list|(
specifier|final
name|String
name|authority
parameter_list|,
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
specifier|final
name|Key
name|k
init|=
operator|new
name|Key
argument_list|(
name|authority
argument_list|,
name|ugi
argument_list|)
decl_stmt|;
name|LeaseRenewer
name|r
init|=
name|renewers
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
operator|new
name|LeaseRenewer
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|renewers
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
comment|/** Remove the given renewer. */
DECL|method|remove (final LeaseRenewer r)
specifier|private
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|LeaseRenewer
name|r
parameter_list|)
block|{
specifier|final
name|LeaseRenewer
name|stored
init|=
name|renewers
operator|.
name|get
argument_list|(
name|r
operator|.
name|factorykey
argument_list|)
decl_stmt|;
comment|//Since a renewer may expire, the stored renewer can be different.
if|if
condition|(
name|r
operator|==
name|stored
condition|)
block|{
if|if
condition|(
operator|!
name|r
operator|.
name|clientsRunning
argument_list|()
condition|)
block|{
name|renewers
operator|.
name|remove
argument_list|(
name|r
operator|.
name|factorykey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** The time in milliseconds that the map became empty. */
DECL|field|emptyTime
specifier|private
name|long
name|emptyTime
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** A fixed lease renewal time period in milliseconds */
DECL|field|renewal
specifier|private
name|long
name|renewal
init|=
name|HdfsConstants
operator|.
name|LEASE_SOFTLIMIT_PERIOD
operator|/
literal|2
decl_stmt|;
comment|/** A daemon for renewing lease */
DECL|field|daemon
specifier|private
name|Daemon
name|daemon
init|=
literal|null
decl_stmt|;
comment|/** Only the daemon with currentId should run. */
DECL|field|currentId
specifier|private
name|int
name|currentId
init|=
literal|0
decl_stmt|;
comment|/**    * A period in milliseconds that the lease renewer thread should run    * after the map became empty.    * In other words,    * if the map is empty for a time period longer than the grace period,    * the renewer should terminate.    */
DECL|field|gracePeriod
specifier|private
name|long
name|gracePeriod
decl_stmt|;
comment|/**    * The time period in milliseconds    * that the renewer sleeps for each iteration.    */
DECL|field|sleepPeriod
specifier|private
name|long
name|sleepPeriod
decl_stmt|;
DECL|field|factorykey
specifier|private
specifier|final
name|Factory
operator|.
name|Key
name|factorykey
decl_stmt|;
comment|/** A list of clients corresponding to this renewer. */
DECL|field|dfsclients
specifier|private
specifier|final
name|List
argument_list|<
name|DFSClient
argument_list|>
name|dfsclients
init|=
operator|new
name|ArrayList
argument_list|<
name|DFSClient
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * A stringified stack trace of the call stack when the Lease Renewer    * was instantiated. This is only generated if trace-level logging is    * enabled on this class.    */
DECL|field|instantiationTrace
specifier|private
specifier|final
name|String
name|instantiationTrace
decl_stmt|;
DECL|method|LeaseRenewer (Factory.Key factorykey)
specifier|private
name|LeaseRenewer
parameter_list|(
name|Factory
operator|.
name|Key
name|factorykey
parameter_list|)
block|{
name|this
operator|.
name|factorykey
operator|=
name|factorykey
expr_stmt|;
name|unsyncSetGraceSleepPeriod
argument_list|(
name|LEASE_RENEWER_GRACE_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|instantiationTrace
operator|=
name|StringUtils
operator|.
name|stringifyException
argument_list|(
operator|new
name|Throwable
argument_list|(
literal|"TRACE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instantiationTrace
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** @return the renewal time in milliseconds. */
DECL|method|getRenewalTime ()
specifier|private
specifier|synchronized
name|long
name|getRenewalTime
parameter_list|()
block|{
return|return
name|renewal
return|;
block|}
comment|/** Used for testing only. */
annotation|@
name|VisibleForTesting
DECL|method|setRenewalTime (final long renewal)
specifier|public
specifier|synchronized
name|void
name|setRenewalTime
parameter_list|(
specifier|final
name|long
name|renewal
parameter_list|)
block|{
name|this
operator|.
name|renewal
operator|=
name|renewal
expr_stmt|;
block|}
comment|/** Add a client. */
DECL|method|addClient (final DFSClient dfsc)
specifier|private
specifier|synchronized
name|void
name|addClient
parameter_list|(
specifier|final
name|DFSClient
name|dfsc
parameter_list|)
block|{
for|for
control|(
name|DFSClient
name|c
range|:
name|dfsclients
control|)
block|{
if|if
condition|(
name|c
operator|==
name|dfsc
condition|)
block|{
comment|//client already exists, nothing to do.
return|return;
block|}
block|}
comment|//client not found, add it
name|dfsclients
operator|.
name|add
argument_list|(
name|dfsc
argument_list|)
expr_stmt|;
comment|//update renewal time
specifier|final
name|int
name|hdfsTimeout
init|=
name|dfsc
operator|.
name|getConf
argument_list|()
operator|.
name|getHdfsTimeout
argument_list|()
decl_stmt|;
if|if
condition|(
name|hdfsTimeout
operator|>
literal|0
condition|)
block|{
specifier|final
name|long
name|half
init|=
name|hdfsTimeout
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|half
operator|<
name|renewal
condition|)
block|{
name|this
operator|.
name|renewal
operator|=
name|half
expr_stmt|;
block|}
block|}
block|}
DECL|method|clientsRunning ()
specifier|private
specifier|synchronized
name|boolean
name|clientsRunning
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DFSClient
argument_list|>
name|i
init|=
name|dfsclients
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|i
operator|.
name|next
argument_list|()
operator|.
name|isClientRunning
argument_list|()
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|!
name|dfsclients
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getSleepPeriod ()
specifier|private
specifier|synchronized
name|long
name|getSleepPeriod
parameter_list|()
block|{
return|return
name|sleepPeriod
return|;
block|}
comment|/** Set the grace period and adjust the sleep period accordingly. */
DECL|method|setGraceSleepPeriod (final long gracePeriod)
specifier|synchronized
name|void
name|setGraceSleepPeriod
parameter_list|(
specifier|final
name|long
name|gracePeriod
parameter_list|)
block|{
name|unsyncSetGraceSleepPeriod
argument_list|(
name|gracePeriod
argument_list|)
expr_stmt|;
block|}
DECL|method|unsyncSetGraceSleepPeriod (final long gracePeriod)
specifier|private
name|void
name|unsyncSetGraceSleepPeriod
parameter_list|(
specifier|final
name|long
name|gracePeriod
parameter_list|)
block|{
if|if
condition|(
name|gracePeriod
operator|<
literal|100L
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|gracePeriod
operator|+
literal|" = gracePeriod< 100ms is too small."
argument_list|)
throw|;
block|}
name|this
operator|.
name|gracePeriod
operator|=
name|gracePeriod
expr_stmt|;
specifier|final
name|long
name|half
init|=
name|gracePeriod
operator|/
literal|2
decl_stmt|;
name|this
operator|.
name|sleepPeriod
operator|=
name|half
operator|<
name|LEASE_RENEWER_SLEEP_DEFAULT
condition|?
name|half
else|:
name|LEASE_RENEWER_SLEEP_DEFAULT
expr_stmt|;
block|}
comment|/** Is the daemon running? */
DECL|method|isRunning ()
specifier|synchronized
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|daemon
operator|!=
literal|null
operator|&&
name|daemon
operator|.
name|isAlive
argument_list|()
return|;
block|}
comment|/** Does this renewer have nothing to renew? */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|dfsclients
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/** Used only by tests */
DECL|method|getDaemonName ()
specifier|synchronized
name|String
name|getDaemonName
parameter_list|()
block|{
return|return
name|daemon
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/** Is the empty period longer than the grace period? */
DECL|method|isRenewerExpired ()
specifier|private
specifier|synchronized
name|boolean
name|isRenewerExpired
parameter_list|()
block|{
return|return
name|emptyTime
operator|!=
name|Long
operator|.
name|MAX_VALUE
operator|&&
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|emptyTime
operator|>
name|gracePeriod
return|;
block|}
DECL|method|put (final long inodeId, final DFSOutputStream out, final DFSClient dfsc)
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
specifier|final
name|long
name|inodeId
parameter_list|,
specifier|final
name|DFSOutputStream
name|out
parameter_list|,
specifier|final
name|DFSClient
name|dfsc
parameter_list|)
block|{
if|if
condition|(
name|dfsc
operator|.
name|isClientRunning
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|isRunning
argument_list|()
operator|||
name|isRenewerExpired
argument_list|()
condition|)
block|{
comment|//start a new deamon with a new id.
specifier|final
name|int
name|id
init|=
operator|++
name|currentId
decl_stmt|;
name|daemon
operator|=
operator|new
name|Daemon
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lease renewer daemon for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" with renew id "
operator|+
name|id
operator|+
literal|" started"
argument_list|)
expr_stmt|;
block|}
name|LeaseRenewer
operator|.
name|this
operator|.
name|run
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|LeaseRenewer
operator|.
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" is interrupted."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|LeaseRenewer
operator|.
name|this
init|)
block|{
name|Factory
operator|.
name|INSTANCE
operator|.
name|remove
argument_list|(
name|LeaseRenewer
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lease renewer daemon for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" with renew id "
operator|+
name|id
operator|+
literal|" exited"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|LeaseRenewer
operator|.
name|this
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|daemon
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|dfsc
operator|.
name|putFileBeingWritten
argument_list|(
name|inodeId
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|emptyTime
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|setEmptyTime (long time)
specifier|synchronized
name|void
name|setEmptyTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|emptyTime
operator|=
name|time
expr_stmt|;
block|}
comment|/** Close a file. */
DECL|method|closeFile (final long inodeId, final DFSClient dfsc)
specifier|public
name|void
name|closeFile
parameter_list|(
specifier|final
name|long
name|inodeId
parameter_list|,
specifier|final
name|DFSClient
name|dfsc
parameter_list|)
block|{
name|dfsc
operator|.
name|removeFileBeingWritten
argument_list|(
name|inodeId
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|dfsc
operator|.
name|isFilesBeingWrittenEmpty
argument_list|()
condition|)
block|{
name|dfsclients
operator|.
name|remove
argument_list|(
name|dfsc
argument_list|)
expr_stmt|;
block|}
comment|//update emptyTime if necessary
if|if
condition|(
name|emptyTime
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
for|for
control|(
name|DFSClient
name|c
range|:
name|dfsclients
control|)
block|{
if|if
condition|(
operator|!
name|c
operator|.
name|isFilesBeingWrittenEmpty
argument_list|()
condition|)
block|{
comment|//found a non-empty file-being-written map
return|return;
block|}
block|}
comment|//discover the first time that all file-being-written maps are empty.
name|emptyTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Close the given client. */
DECL|method|closeClient (final DFSClient dfsc)
specifier|public
specifier|synchronized
name|void
name|closeClient
parameter_list|(
specifier|final
name|DFSClient
name|dfsc
parameter_list|)
block|{
name|dfsclients
operator|.
name|remove
argument_list|(
name|dfsc
argument_list|)
expr_stmt|;
if|if
condition|(
name|dfsclients
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|isRunning
argument_list|()
operator|||
name|isRenewerExpired
argument_list|()
condition|)
block|{
name|Factory
operator|.
name|INSTANCE
operator|.
name|remove
argument_list|(
name|LeaseRenewer
operator|.
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|emptyTime
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
comment|//discover the first time that the client list is empty.
name|emptyTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
block|}
comment|//update renewal time
if|if
condition|(
name|renewal
operator|==
name|dfsc
operator|.
name|getConf
argument_list|()
operator|.
name|getHdfsTimeout
argument_list|()
operator|/
literal|2
condition|)
block|{
name|long
name|min
init|=
name|HdfsConstants
operator|.
name|LEASE_SOFTLIMIT_PERIOD
decl_stmt|;
for|for
control|(
name|DFSClient
name|c
range|:
name|dfsclients
control|)
block|{
specifier|final
name|int
name|timeout
init|=
name|c
operator|.
name|getConf
argument_list|()
operator|.
name|getHdfsTimeout
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeout
operator|>
literal|0
operator|&&
name|timeout
operator|<
name|min
condition|)
block|{
name|min
operator|=
name|timeout
expr_stmt|;
block|}
block|}
name|renewal
operator|=
name|min
operator|/
literal|2
expr_stmt|;
block|}
block|}
DECL|method|interruptAndJoin ()
specifier|public
name|void
name|interruptAndJoin
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Daemon
name|daemonCopy
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|isRunning
argument_list|()
condition|)
block|{
name|daemon
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|daemonCopy
operator|=
name|daemon
expr_stmt|;
block|}
block|}
if|if
condition|(
name|daemonCopy
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Wait for lease checker to terminate"
argument_list|)
expr_stmt|;
block|}
name|daemonCopy
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|renew ()
specifier|private
name|void
name|renew
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|DFSClient
argument_list|>
name|copies
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|copies
operator|=
operator|new
name|ArrayList
argument_list|<
name|DFSClient
argument_list|>
argument_list|(
name|dfsclients
argument_list|)
expr_stmt|;
block|}
comment|//sort the client names for finding out repeated names.
name|Collections
operator|.
name|sort
argument_list|(
name|copies
argument_list|,
operator|new
name|Comparator
argument_list|<
name|DFSClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|DFSClient
name|left
parameter_list|,
specifier|final
name|DFSClient
name|right
parameter_list|)
block|{
return|return
name|left
operator|.
name|getClientName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|right
operator|.
name|getClientName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|String
name|previousName
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|copies
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|DFSClient
name|c
init|=
name|copies
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//skip if current client name is the same as the previous name.
if|if
condition|(
operator|!
name|c
operator|.
name|getClientName
argument_list|()
operator|.
name|equals
argument_list|(
name|previousName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|c
operator|.
name|renewLease
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Did not renew lease for client "
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
name|previousName
operator|=
name|c
operator|.
name|getClientName
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lease renewed for client "
operator|+
name|previousName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Periodically check in with the namenode and renew all the leases    * when the lease period is half over.    */
DECL|method|run (final int id)
specifier|private
name|void
name|run
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|long
name|lastRenewed
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
init|;
operator|!
name|Thread
operator|.
name|interrupted
argument_list|()
condition|;
name|Thread
operator|.
name|sleep
argument_list|(
name|getSleepPeriod
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|long
name|elapsed
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|lastRenewed
decl_stmt|;
if|if
condition|(
name|elapsed
operator|>=
name|getRenewalTime
argument_list|()
condition|)
block|{
try|try
block|{
name|renew
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lease renewer daemon for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" with renew id "
operator|+
name|id
operator|+
literal|" executed"
argument_list|)
expr_stmt|;
block|}
name|lastRenewed
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to renew lease for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" for "
operator|+
operator|(
name|elapsed
operator|/
literal|1000
operator|)
operator|+
literal|" seconds.  Aborting ..."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
operator|!
name|dfsclients
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|DFSClient
name|dfsClient
init|=
name|dfsclients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|dfsClient
operator|.
name|closeAllFilesBeingWritten
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|closeClient
argument_list|(
name|dfsClient
argument_list|)
expr_stmt|;
block|}
comment|//Expire the current LeaseRenewer thread.
name|emptyTime
operator|=
literal|0
expr_stmt|;
block|}
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to renew lease for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" for "
operator|+
operator|(
name|elapsed
operator|/
literal|1000
operator|)
operator|+
literal|" seconds.  Will retry shortly ..."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|id
operator|!=
name|currentId
operator|||
name|isRenewerExpired
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|id
operator|!=
name|currentId
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lease renewer daemon for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" with renew id "
operator|+
name|id
operator|+
literal|" is not current"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lease renewer daemon for "
operator|+
name|clientsString
argument_list|()
operator|+
literal|" with renew id "
operator|+
name|id
operator|+
literal|" expired"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//no longer the current daemon or expired
return|return;
block|}
comment|// if no clients are in running state or there is no more clients
comment|// registered with this renewer, stop the daemon after the grace
comment|// period.
if|if
condition|(
operator|!
name|clientsRunning
argument_list|()
operator|&&
name|emptyTime
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|emptyTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|":"
operator|+
name|factorykey
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
return|return
name|s
operator|+
literal|", clients="
operator|+
name|clientsString
argument_list|()
operator|+
literal|", created at "
operator|+
name|instantiationTrace
return|;
block|}
return|return
name|s
return|;
block|}
comment|/** Get the names of all clients */
DECL|method|clientsString ()
specifier|private
specifier|synchronized
name|String
name|clientsString
parameter_list|()
block|{
if|if
condition|(
name|dfsclients
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"[]"
return|;
block|}
else|else
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|dfsclients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClientName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|dfsclients
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|dfsclients
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getClientName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

