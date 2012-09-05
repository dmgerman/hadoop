begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
package|;
end_package

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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|*
import|;
end_import

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
name|Arrays
import|;
end_import

begin_class
DECL|class|TestReconfiguration
specifier|public
class|class
name|TestReconfiguration
block|{
DECL|field|conf1
specifier|private
name|Configuration
name|conf1
decl_stmt|;
DECL|field|conf2
specifier|private
name|Configuration
name|conf2
decl_stmt|;
DECL|field|PROP1
specifier|private
specifier|static
specifier|final
name|String
name|PROP1
init|=
literal|"test.prop.one"
decl_stmt|;
DECL|field|PROP2
specifier|private
specifier|static
specifier|final
name|String
name|PROP2
init|=
literal|"test.prop.two"
decl_stmt|;
DECL|field|PROP3
specifier|private
specifier|static
specifier|final
name|String
name|PROP3
init|=
literal|"test.prop.three"
decl_stmt|;
DECL|field|PROP4
specifier|private
specifier|static
specifier|final
name|String
name|PROP4
init|=
literal|"test.prop.four"
decl_stmt|;
DECL|field|PROP5
specifier|private
specifier|static
specifier|final
name|String
name|PROP5
init|=
literal|"test.prop.five"
decl_stmt|;
DECL|field|VAL1
specifier|private
specifier|static
specifier|final
name|String
name|VAL1
init|=
literal|"val1"
decl_stmt|;
DECL|field|VAL2
specifier|private
specifier|static
specifier|final
name|String
name|VAL2
init|=
literal|"val2"
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|conf1
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf2
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|// set some test properties
name|conf1
operator|.
name|set
argument_list|(
name|PROP1
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
name|conf1
operator|.
name|set
argument_list|(
name|PROP2
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
name|conf1
operator|.
name|set
argument_list|(
name|PROP3
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|PROP1
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
comment|// same as conf1
name|conf2
operator|.
name|set
argument_list|(
name|PROP2
argument_list|,
name|VAL2
argument_list|)
expr_stmt|;
comment|// different value as conf1
comment|// PROP3 not set in conf2
name|conf2
operator|.
name|set
argument_list|(
name|PROP4
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
comment|// not set in conf1
block|}
comment|/**    * Test ReconfigurationUtil.getChangedProperties.    */
annotation|@
name|Test
DECL|method|testGetChangedProperties ()
specifier|public
name|void
name|testGetChangedProperties
parameter_list|()
block|{
name|Collection
argument_list|<
name|ReconfigurationUtil
operator|.
name|PropertyChange
argument_list|>
name|changes
init|=
name|ReconfigurationUtil
operator|.
name|getChangedProperties
argument_list|(
name|conf2
argument_list|,
name|conf1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected 3 changed properties but got "
operator|+
name|changes
operator|.
name|size
argument_list|()
argument_list|,
name|changes
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|boolean
name|changeFound
init|=
literal|false
decl_stmt|;
name|boolean
name|unsetFound
init|=
literal|false
decl_stmt|;
name|boolean
name|setFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ReconfigurationUtil
operator|.
name|PropertyChange
name|c
range|:
name|changes
control|)
block|{
if|if
condition|(
name|c
operator|.
name|prop
operator|.
name|equals
argument_list|(
name|PROP2
argument_list|)
operator|&&
name|c
operator|.
name|oldVal
operator|!=
literal|null
operator|&&
name|c
operator|.
name|oldVal
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
operator|&&
name|c
operator|.
name|newVal
operator|!=
literal|null
operator|&&
name|c
operator|.
name|newVal
operator|.
name|equals
argument_list|(
name|VAL2
argument_list|)
condition|)
block|{
name|changeFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|prop
operator|.
name|equals
argument_list|(
name|PROP3
argument_list|)
operator|&&
name|c
operator|.
name|oldVal
operator|!=
literal|null
operator|&&
name|c
operator|.
name|oldVal
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
operator|&&
name|c
operator|.
name|newVal
operator|==
literal|null
condition|)
block|{
name|unsetFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|prop
operator|.
name|equals
argument_list|(
name|PROP4
argument_list|)
operator|&&
name|c
operator|.
name|oldVal
operator|==
literal|null
operator|&&
name|c
operator|.
name|newVal
operator|!=
literal|null
operator|&&
name|c
operator|.
name|newVal
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
condition|)
block|{
name|setFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"not all changes have been applied"
argument_list|,
name|changeFound
operator|&&
name|unsetFound
operator|&&
name|setFound
argument_list|)
expr_stmt|;
block|}
comment|/**    * a simple reconfigurable class    */
DECL|class|ReconfigurableDummy
specifier|public
specifier|static
class|class
name|ReconfigurableDummy
extends|extends
name|ReconfigurableBase
implements|implements
name|Runnable
block|{
DECL|field|running
specifier|public
specifier|volatile
name|boolean
name|running
init|=
literal|true
decl_stmt|;
DECL|method|ReconfigurableDummy (Configuration conf)
specifier|public
name|ReconfigurableDummy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReconfigurableProperties ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getReconfigurableProperties
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|PROP1
argument_list|,
name|PROP2
argument_list|,
name|PROP4
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reconfigurePropertyImpl (String property, String newVal)
specifier|public
specifier|synchronized
name|void
name|reconfigurePropertyImpl
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|newVal
parameter_list|)
block|{
comment|// do nothing
block|}
comment|/**      * Run until PROP1 is no longer VAL1.      */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
operator|&&
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
block|}
comment|/**    * Test reconfiguring a Reconfigurable.    */
annotation|@
name|Test
DECL|method|testReconfigure ()
specifier|public
name|void
name|testReconfigure
parameter_list|()
block|{
name|ReconfigurableDummy
name|dummy
init|=
operator|new
name|ReconfigurableDummy
argument_list|(
name|conf1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|PROP1
operator|+
literal|" set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP2
operator|+
literal|" set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP2
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP3
operator|+
literal|" set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP3
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP4
operator|+
literal|" set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP4
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP5
operator|+
literal|" set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP5
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP1
operator|+
literal|" should be reconfigurable "
argument_list|,
name|dummy
operator|.
name|isPropertyReconfigurable
argument_list|(
name|PROP1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP2
operator|+
literal|" should be reconfigurable "
argument_list|,
name|dummy
operator|.
name|isPropertyReconfigurable
argument_list|(
name|PROP2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|PROP3
operator|+
literal|" should not be reconfigurable "
argument_list|,
name|dummy
operator|.
name|isPropertyReconfigurable
argument_list|(
name|PROP3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP4
operator|+
literal|" should be reconfigurable "
argument_list|,
name|dummy
operator|.
name|isPropertyReconfigurable
argument_list|(
name|PROP4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|PROP5
operator|+
literal|" should not be reconfigurable "
argument_list|,
name|dummy
operator|.
name|isPropertyReconfigurable
argument_list|(
name|PROP5
argument_list|)
argument_list|)
expr_stmt|;
comment|// change something to the same value as before
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP1
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP1
operator|+
literal|" set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"received unexpected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// change something to null
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP1
operator|+
literal|"set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"received unexpected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// change something to a different value than before
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP1
argument_list|,
name|VAL2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP1
operator|+
literal|"set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"received unexpected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// set unset property to null
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP4
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP4
operator|+
literal|"set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP4
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"received unexpected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// set unset property
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP4
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PROP4
operator|+
literal|"set to wrong value "
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP4
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"received unexpected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// try to set unset property to null (not reconfigurable)
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP5
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"did not receive expected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// try to set unset property to value (not reconfigurable)
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP5
argument_list|,
name|VAL1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"did not receive expected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// try to change property to value (not reconfigurable)
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP3
argument_list|,
name|VAL2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"did not receive expected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
comment|// try to change property to null (not reconfigurable)
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP3
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"did not receive expected exception"
argument_list|,
name|exceptionCaught
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test whether configuration changes are visible in another thread.    */
annotation|@
name|Test
DECL|method|testThread ()
specifier|public
name|void
name|testThread
parameter_list|()
throws|throws
name|ReconfigurationException
block|{
name|ReconfigurableDummy
name|dummy
init|=
operator|new
name|ReconfigurableDummy
argument_list|(
name|conf1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL1
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
name|dummyThread
init|=
operator|new
name|Thread
argument_list|(
name|dummy
argument_list|)
decl_stmt|;
name|dummyThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{
comment|// do nothing
block|}
name|dummy
operator|.
name|reconfigureProperty
argument_list|(
name|PROP1
argument_list|,
name|VAL2
argument_list|)
expr_stmt|;
name|long
name|endWait
init|=
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|2000
decl_stmt|;
while|while
condition|(
name|dummyThread
operator|.
name|isAlive
argument_list|()
operator|&&
name|Time
operator|.
name|now
argument_list|()
operator|<
name|endWait
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
name|assertFalse
argument_list|(
literal|"dummy thread should not be alive"
argument_list|,
name|dummyThread
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
name|dummy
operator|.
name|running
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|dummyThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{
comment|// do nothing
block|}
name|assertTrue
argument_list|(
name|PROP1
operator|+
literal|" is set to wrong value"
argument_list|,
name|dummy
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROP1
argument_list|)
operator|.
name|equals
argument_list|(
name|VAL2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

