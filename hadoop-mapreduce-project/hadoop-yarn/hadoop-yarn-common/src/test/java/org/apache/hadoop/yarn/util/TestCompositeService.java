begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|yarn
operator|.
name|YarnException
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
name|service
operator|.
name|Service
operator|.
name|STATE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCompositeService
specifier|public
class|class
name|TestCompositeService
block|{
DECL|field|NUM_OF_SERVICES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_OF_SERVICES
init|=
literal|5
decl_stmt|;
DECL|field|FAILED_SERVICE_SEQ_NUMBER
specifier|private
specifier|static
specifier|final
name|int
name|FAILED_SERVICE_SEQ_NUMBER
init|=
literal|2
decl_stmt|;
annotation|@
name|Test
DECL|method|testCallSequence ()
specifier|public
name|void
name|testCallSequence
parameter_list|()
block|{
name|ServiceManager
name|serviceManager
init|=
operator|new
name|ServiceManager
argument_list|(
literal|"ServiceManager"
argument_list|)
decl_stmt|;
comment|// Add services
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|CompositeServiceImpl
name|service
init|=
operator|new
name|CompositeServiceImpl
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|serviceManager
operator|.
name|addTestService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
name|CompositeServiceImpl
index|[]
name|services
init|=
name|serviceManager
operator|.
name|getServices
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|CompositeServiceImpl
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of registered services "
argument_list|,
name|NUM_OF_SERVICES
argument_list|,
name|services
operator|.
name|length
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Initialise the composite service
name|serviceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Verify the init() call sequence numbers for every service
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"For "
operator|+
name|services
index|[
name|i
index|]
operator|+
literal|" service, init() call sequence number should have been "
argument_list|,
name|i
argument_list|,
name|services
index|[
name|i
index|]
operator|.
name|getCallSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Reset the call sequence numbers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|services
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|serviceManager
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Verify the start() call sequence numbers for every service
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"For "
operator|+
name|services
index|[
name|i
index|]
operator|+
literal|" service, start() call sequence number should have been "
argument_list|,
name|i
argument_list|,
name|services
index|[
name|i
index|]
operator|.
name|getCallSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Reset the call sequence numbers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|services
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|serviceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Verify the stop() call sequence numbers for every service
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"For "
operator|+
name|services
index|[
name|i
index|]
operator|+
literal|" service, stop() call sequence number should have been "
argument_list|,
operator|(
operator|(
name|NUM_OF_SERVICES
operator|-
literal|1
operator|)
operator|-
name|i
operator|)
argument_list|,
name|services
index|[
name|i
index|]
operator|.
name|getCallSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testServiceStartup ()
specifier|public
name|void
name|testServiceStartup
parameter_list|()
block|{
name|ServiceManager
name|serviceManager
init|=
operator|new
name|ServiceManager
argument_list|(
literal|"ServiceManager"
argument_list|)
decl_stmt|;
comment|// Add services
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|CompositeServiceImpl
name|service
init|=
operator|new
name|CompositeServiceImpl
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|FAILED_SERVICE_SEQ_NUMBER
condition|)
block|{
name|service
operator|.
name|setThrowExceptionOnStart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|serviceManager
operator|.
name|addTestService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
name|CompositeServiceImpl
index|[]
name|services
init|=
name|serviceManager
operator|.
name|getServices
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|CompositeServiceImpl
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Initialise the composite service
name|serviceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Start the composite service
try|try
block|{
name|serviceManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Exception should have been thrown due to startup failure of last service"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>=
name|FAILED_SERVICE_SEQ_NUMBER
condition|)
block|{
comment|// Failed service state should be INITED
name|assertEquals
argument_list|(
literal|"Service state should have been "
argument_list|,
name|STATE
operator|.
name|INITED
argument_list|,
name|services
index|[
name|NUM_OF_SERVICES
operator|-
literal|1
index|]
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Service state should have been "
argument_list|,
name|STATE
operator|.
name|STOPPED
argument_list|,
name|services
index|[
name|i
index|]
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testServiceStop ()
specifier|public
name|void
name|testServiceStop
parameter_list|()
block|{
name|ServiceManager
name|serviceManager
init|=
operator|new
name|ServiceManager
argument_list|(
literal|"ServiceManager"
argument_list|)
decl_stmt|;
comment|// Add services
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
condition|;
name|i
operator|++
control|)
block|{
name|CompositeServiceImpl
name|service
init|=
operator|new
name|CompositeServiceImpl
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|FAILED_SERVICE_SEQ_NUMBER
condition|)
block|{
name|service
operator|.
name|setThrowExceptionOnStop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|serviceManager
operator|.
name|addTestService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
name|CompositeServiceImpl
index|[]
name|services
init|=
name|serviceManager
operator|.
name|getServices
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|CompositeServiceImpl
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Initialise the composite service
name|serviceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|serviceManager
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Start the composite service
try|try
block|{
name|serviceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OF_SERVICES
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Service state should have been "
argument_list|,
name|STATE
operator|.
name|STOPPED
argument_list|,
name|services
index|[
name|NUM_OF_SERVICES
index|]
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CompositeServiceImpl
specifier|public
specifier|static
class|class
name|CompositeServiceImpl
extends|extends
name|CompositeService
block|{
DECL|field|counter
specifier|private
specifier|static
name|int
name|counter
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|callSequenceNumber
specifier|private
name|int
name|callSequenceNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|throwExceptionOnStart
specifier|private
name|boolean
name|throwExceptionOnStart
decl_stmt|;
DECL|field|throwExceptionOnStop
specifier|private
name|boolean
name|throwExceptionOnStop
decl_stmt|;
DECL|method|CompositeServiceImpl (int sequenceNumber)
specifier|public
name|CompositeServiceImpl
parameter_list|(
name|int
name|sequenceNumber
parameter_list|)
block|{
name|super
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|sequenceNumber
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|counter
operator|++
expr_stmt|;
name|callSequenceNumber
operator|=
name|counter
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|throwExceptionOnStart
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Fake service start exception"
argument_list|)
throw|;
block|}
name|counter
operator|++
expr_stmt|;
name|callSequenceNumber
operator|=
name|counter
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|counter
operator|++
expr_stmt|;
name|callSequenceNumber
operator|=
name|counter
expr_stmt|;
if|if
condition|(
name|throwExceptionOnStop
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Fake service stop exception"
argument_list|)
throw|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getCounter ()
specifier|public
specifier|static
name|int
name|getCounter
parameter_list|()
block|{
return|return
name|counter
return|;
block|}
DECL|method|getCallSequenceNumber ()
specifier|public
name|int
name|getCallSequenceNumber
parameter_list|()
block|{
return|return
name|callSequenceNumber
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|callSequenceNumber
operator|=
operator|-
literal|1
expr_stmt|;
name|counter
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|setThrowExceptionOnStart (boolean throwExceptionOnStart)
specifier|public
name|void
name|setThrowExceptionOnStart
parameter_list|(
name|boolean
name|throwExceptionOnStart
parameter_list|)
block|{
name|this
operator|.
name|throwExceptionOnStart
operator|=
name|throwExceptionOnStart
expr_stmt|;
block|}
DECL|method|setThrowExceptionOnStop (boolean throwExceptionOnStop)
specifier|public
name|void
name|setThrowExceptionOnStop
parameter_list|(
name|boolean
name|throwExceptionOnStop
parameter_list|)
block|{
name|this
operator|.
name|throwExceptionOnStop
operator|=
name|throwExceptionOnStop
expr_stmt|;
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
literal|"Service "
operator|+
name|getName
argument_list|()
return|;
block|}
block|}
DECL|class|ServiceManager
specifier|public
specifier|static
class|class
name|ServiceManager
extends|extends
name|CompositeService
block|{
DECL|method|addTestService (CompositeService service)
specifier|public
name|void
name|addTestService
parameter_list|(
name|CompositeService
name|service
parameter_list|)
block|{
name|addService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
DECL|method|ServiceManager (String name)
specifier|public
name|ServiceManager
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

