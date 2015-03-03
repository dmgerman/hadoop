begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.aggregator
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
name|timelineservice
operator|.
name|aggregator
package|;
end_package

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
name|HashMap
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|http
operator|.
name|HttpServer2
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
name|http
operator|.
name|lib
operator|.
name|StaticUserWebFilter
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
name|CompositeService
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|webapp
operator|.
name|GenericExceptionHandler
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
name|webapp
operator|.
name|YarnJacksonJaxbJsonProvider
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
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
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
name|CommonConfigurationKeys
operator|.
name|DEFAULT_HADOOP_HTTP_STATIC_USER
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
name|CommonConfigurationKeys
operator|.
name|HADOOP_HTTP_STATIC_USER
import|;
end_import

begin_comment
comment|/**  * Class that manages adding and removing aggregators and their lifecycle. It  * provides thread safety access to the aggregators inside.  *  * It is a singleton, and instances should be obtained via  * {@link #getInstance()}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineAggregatorsCollection
specifier|public
class|class
name|TimelineAggregatorsCollection
extends|extends
name|CompositeService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TimelineAggregatorsCollection
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|TimelineAggregatorsCollection
name|INSTANCE
init|=
operator|new
name|TimelineAggregatorsCollection
argument_list|()
decl_stmt|;
comment|// access to this map is synchronized with the map itself
DECL|field|aggregators
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TimelineAggregator
argument_list|>
name|aggregators
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TimelineAggregator
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// REST server for this aggregator collection
DECL|field|timelineRestServer
specifier|private
name|HttpServer2
name|timelineRestServer
decl_stmt|;
DECL|field|AGGREGATOR_COLLECTION_ATTR_KEY
specifier|static
specifier|final
name|String
name|AGGREGATOR_COLLECTION_ATTR_KEY
init|=
literal|"aggregator.collection"
decl_stmt|;
DECL|method|getInstance ()
specifier|static
name|TimelineAggregatorsCollection
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
DECL|method|TimelineAggregatorsCollection ()
name|TimelineAggregatorsCollection
parameter_list|()
block|{
name|super
argument_list|(
name|TimelineAggregatorsCollection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|startWebApp
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|timelineRestServer
operator|!=
literal|null
condition|)
block|{
name|timelineRestServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Put the aggregator into the collection if an aggregator mapped by id does    * not exist.    *    * @throws YarnRuntimeException if there was any exception in initializing and    * starting the app level service    * @return the aggregator associated with id after the potential put.    */
DECL|method|putIfAbsent (String id, TimelineAggregator aggregator)
specifier|public
name|TimelineAggregator
name|putIfAbsent
parameter_list|(
name|String
name|id
parameter_list|,
name|TimelineAggregator
name|aggregator
parameter_list|)
block|{
synchronized|synchronized
init|(
name|aggregators
init|)
block|{
name|TimelineAggregator
name|aggregatorInTable
init|=
name|aggregators
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregatorInTable
operator|==
literal|null
condition|)
block|{
try|try
block|{
comment|// initialize, start, and add it to the collection so it can be
comment|// cleaned up when the parent shuts down
name|aggregator
operator|.
name|init
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|aggregator
operator|.
name|start
argument_list|()
expr_stmt|;
name|aggregators
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"the aggregator for "
operator|+
name|id
operator|+
literal|" was added"
argument_list|)
expr_stmt|;
return|return
name|aggregator
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"the aggregator for "
operator|+
name|id
operator|+
literal|" already exists!"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
name|aggregatorInTable
return|;
block|}
block|}
block|}
comment|/**    * Removes the aggregator for the specified id. The aggregator is also stopped    * as a result. If the aggregator does not exist, no change is made.    *    * @return whether it was removed successfully    */
DECL|method|remove (String id)
specifier|public
name|boolean
name|remove
parameter_list|(
name|String
name|id
parameter_list|)
block|{
synchronized|synchronized
init|(
name|aggregators
init|)
block|{
name|TimelineAggregator
name|aggregator
init|=
name|aggregators
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"the aggregator for "
operator|+
name|id
operator|+
literal|" does not exist!"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// stop the service to do clean up
name|aggregator
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"the aggregator service for "
operator|+
name|id
operator|+
literal|" was removed"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
comment|/**    * Returns the aggregator for the specified id.    *    * @return the aggregator or null if it does not exist    */
DECL|method|get (String id)
specifier|public
name|TimelineAggregator
name|get
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|aggregators
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Returns whether the aggregator for the specified id exists in this    * collection.    */
DECL|method|containsKey (String id)
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|aggregators
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Launch the REST web server for this aggregator collection    */
DECL|method|startWebApp ()
specifier|private
name|void
name|startWebApp
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
comment|// use the same ports as the old ATS for now; we could create new properties
comment|// for the new timeline service if needed
name|String
name|bindAddress
init|=
name|WebAppUtils
operator|.
name|getWebAppBindURL
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiating the per-node aggregator webapp at "
operator|+
name|bindAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|confForInfoServer
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|confForInfoServer
operator|.
name|setInt
argument_list|(
name|HttpServer2
operator|.
name|HTTP_MAX_THREADS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"timeline"
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"http://"
operator|+
name|bindAddress
argument_list|)
argument_list|)
decl_stmt|;
name|timelineRestServer
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// TODO: replace this by an authentication filter in future.
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|username
init|=
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_HTTP_STATIC_USER
argument_list|,
name|DEFAULT_HADOOP_HTTP_STATIC_USER
argument_list|)
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
name|HADOOP_HTTP_STATIC_USER
argument_list|,
name|username
argument_list|)
expr_stmt|;
name|HttpServer2
operator|.
name|defineFilter
argument_list|(
name|timelineRestServer
operator|.
name|getWebAppContext
argument_list|()
argument_list|,
literal|"static_user_filter_timeline"
argument_list|,
name|StaticUserWebFilter
operator|.
name|StaticUserFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|options
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/*"
block|}
argument_list|)
expr_stmt|;
name|timelineRestServer
operator|.
name|addJerseyResourcePackage
argument_list|(
name|TimelineAggregatorWebService
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|";"
operator|+
name|GenericExceptionHandler
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|";"
operator|+
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
name|timelineRestServer
operator|.
name|setAttribute
argument_list|(
name|AGGREGATOR_COLLECTION_ATTR_KEY
argument_list|,
name|TimelineAggregatorsCollection
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|timelineRestServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"The per-node aggregator webapp failed to start."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

