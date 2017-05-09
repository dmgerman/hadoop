begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|FairSchedulerUtilities
operator|.
name|trimQueueName
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
name|assertNull
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for {@link FairSchedulerUtilities}.  */
end_comment

begin_class
DECL|class|TestFairSchedulerUtilities
specifier|public
class|class
name|TestFairSchedulerUtilities
block|{
annotation|@
name|Test
DECL|method|testTrimQueueNameEquals ()
specifier|public
name|void
name|testTrimQueueNameEquals
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
index|[]
name|equalsStrings
init|=
block|{
comment|// no spaces
literal|"a"
block|,
comment|// leading spaces
literal|" a"
block|,
literal|" \u3000a"
block|,
literal|"\u2002\u3000\r\u0085\u200A\u2005\u2000\u3000a"
block|,
literal|"\u2029\u000B\u3000\u2008\u2003\u205F\u3000\u1680a"
block|,
literal|"\u0009\u0020\u2006\u2001\u202F\u00A0\u000C\u2009a"
block|,
literal|"\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000a"
block|,
comment|// trailing spaces
literal|"a\u200A"
block|,
literal|"a  \u0085 "
block|,
comment|// spaces on both sides
literal|" a "
block|,
literal|"  a\u00A0"
block|,
literal|"\u0009\u0020\u2006\u2001\u202F\u00A0\u000C\u2009a"
operator|+
literal|"\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000"
block|,     }
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|equalsStrings
control|)
block|{
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|trimQueueName
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTrimQueueNamesEmpty ()
specifier|public
name|void
name|testTrimQueueNamesEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|trimQueueName
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|spaces
init|=
literal|"\u2002\u3000\r\u0085\u200A\u2005\u2000\u3000"
operator|+
literal|"\u2029\u000B\u3000\u2008\u2003\u205F\u3000\u1680"
operator|+
literal|"\u0009\u0020\u2006\u2001\u202F\u00A0\u000C\u2009"
operator|+
literal|"\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000"
decl_stmt|;
name|assertTrue
argument_list|(
name|trimQueueName
argument_list|(
name|spaces
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

