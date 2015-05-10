/*
 ===========================================================================
   Copyright 2002-2010 Martin Dvorak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ===========================================================================
*/
package com.mindcognition.mindraider;

public class MindRaiderException extends RuntimeException {

	public MindRaiderException() {
		super();
	}

	public MindRaiderException(String arg0) {
		super(arg0);
	}

	public MindRaiderException(Throwable arg0) {
		super(arg0);
	}

	public MindRaiderException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

    private static final long serialVersionUID = 1L;
}
