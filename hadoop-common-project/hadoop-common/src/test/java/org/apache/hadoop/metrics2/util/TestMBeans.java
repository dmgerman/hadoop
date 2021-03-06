begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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

begin_comment
comment|/**  * Test MXBean addition of key/value pairs to registered MBeans.  */
end_comment

begin_class
DECL|class|TestMBeans
specifier|public
class|class
name|TestMBeans
implements|implements
name|DummyMXBean
block|{
DECL|field|counter
specifier|private
name|int
name|counter
init|=
literal|1
decl_stmt|;
annotation|@
name|Test
DECL|method|testRegister ()
specifier|public
name|void
name|testRegister
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|objectName
init|=
literal|null
decl_stmt|;
try|try
block|{
name|counter
operator|=
literal|23
expr_stmt|;
name|objectName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"UnitTest"
argument_list|,
literal|"RegisterTest"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|MBeanServer
name|platformMBeanServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|int
name|jmxCounter
init|=
operator|(
name|int
operator|)
name|platformMBeanServer
operator|.
name|getAttribute
argument_list|(
name|objectName
argument_list|,
literal|"Counter"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|counter
argument_list|,
name|jmxCounter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|objectName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testRegisterWithAdditionalProperties ()
specifier|public
name|void
name|testRegisterWithAdditionalProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|objectName
init|=
literal|null
decl_stmt|;
try|try
block|{
name|counter
operator|=
literal|42
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"flavour"
argument_list|,
literal|"server"
argument_list|)
expr_stmt|;
name|objectName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"UnitTest"
argument_list|,
literal|"RegisterTest"
argument_list|,
name|properties
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|MBeanServer
name|platformMBeanServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|int
name|jmxCounter
init|=
operator|(
name|int
operator|)
name|platformMBeanServer
operator|.
name|getAttribute
argument_list|(
name|objectName
argument_list|,
literal|"Counter"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|counter
argument_list|,
name|jmxCounter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|objectName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetMbeanNameName ()
specifier|public
name|void
name|testGetMbeanNameName
parameter_list|()
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ObjectName
name|mBeanName
init|=
name|MBeans
operator|.
name|getMBeanName
argument_list|(
literal|"Service"
argument_list|,
literal|"Name"
argument_list|,
name|properties
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Service"
argument_list|,
name|MBeans
operator|.
name|getMbeanNameService
argument_list|(
name|mBeanName
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|mBeanName
operator|=
name|MBeans
operator|.
name|getMBeanName
argument_list|(
literal|"Service"
argument_list|,
literal|"Name"
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Service"
argument_list|,
name|MBeans
operator|.
name|getMbeanNameService
argument_list|(
name|mBeanName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCounter ()
specifier|public
name|int
name|getCounter
parameter_list|()
block|{
return|return
name|counter
return|;
block|}
block|}
end_class

end_unit

