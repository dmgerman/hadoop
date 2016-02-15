begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
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
name|assertTrue
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|StatusType
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
name|Attr
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
name|Node
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

begin_class
DECL|class|WebServicesTestUtils
specifier|public
class|class
name|WebServicesTestUtils
block|{
DECL|method|getXmlLong (Element element, String name)
specifier|public
specifier|static
name|long
name|getXmlLong
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|val
init|=
name|getXmlString
argument_list|(
name|element
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|getXmlInt (Element element, String name)
specifier|public
specifier|static
name|int
name|getXmlInt
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|val
init|=
name|getXmlString
argument_list|(
name|element
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|getXmlBoolean (Element element, String name)
specifier|public
specifier|static
name|Boolean
name|getXmlBoolean
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|val
init|=
name|getXmlString
argument_list|(
name|element
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|getXmlFloat (Element element, String name)
specifier|public
specifier|static
name|float
name|getXmlFloat
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|val
init|=
name|getXmlString
argument_list|(
name|element
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|getXmlStrings (Element element, String name)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getXmlStrings
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|NodeList
name|id
init|=
name|element
operator|.
name|getElementsByTagName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|id
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|strings
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|line
init|=
operator|(
name|Element
operator|)
name|id
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Node
name|first
init|=
name|line
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|String
name|val
init|=
name|first
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|strings
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
DECL|method|getXmlString (Element element, String name)
specifier|public
specifier|static
name|String
name|getXmlString
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|NodeList
name|id
init|=
name|element
operator|.
name|getElementsByTagName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Element
name|line
init|=
operator|(
name|Element
operator|)
name|id
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Node
name|first
init|=
name|line
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
comment|// handle empty<key></key>
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|String
name|val
init|=
name|first
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|val
return|;
block|}
DECL|method|getXmlAttrString (Element element, String name)
specifier|public
specifier|static
name|String
name|getXmlAttrString
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Attr
name|at
init|=
name|element
operator|.
name|getAttributeNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|!=
literal|null
condition|)
block|{
return|return
name|at
operator|.
name|getValue
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|checkStringMatch (String print, String expected, String got)
specifier|public
specifier|static
name|void
name|checkStringMatch
parameter_list|(
name|String
name|print
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|got
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|print
operator|+
literal|" doesn't match, got: "
operator|+
name|got
operator|+
literal|" expected: "
operator|+
name|expected
argument_list|,
name|got
operator|.
name|matches
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkStringContains (String print, String expected, String got)
specifier|public
specifier|static
name|void
name|checkStringContains
parameter_list|(
name|String
name|print
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|got
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|print
operator|+
literal|" doesn't contain expected string, got: "
operator|+
name|got
operator|+
literal|" expected: "
operator|+
name|expected
argument_list|,
name|got
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkStringEqual (String print, String expected, String got)
specifier|public
specifier|static
name|void
name|checkStringEqual
parameter_list|(
name|String
name|print
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|got
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|print
operator|+
literal|" is not equal, got: "
operator|+
name|got
operator|+
literal|" expected: "
operator|+
name|expected
argument_list|,
name|got
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResponseStatusCode (StatusType expected, StatusType actual)
specifier|public
specifier|static
name|void
name|assertResponseStatusCode
parameter_list|(
name|StatusType
name|expected
parameter_list|,
name|StatusType
name|actual
parameter_list|)
block|{
name|assertResponseStatusCode
argument_list|(
literal|null
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResponseStatusCode (String errmsg, StatusType expected, StatusType actual)
specifier|public
specifier|static
name|void
name|assertResponseStatusCode
parameter_list|(
name|String
name|errmsg
parameter_list|,
name|StatusType
name|expected
parameter_list|,
name|StatusType
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|errmsg
argument_list|,
name|expected
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|actual
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

